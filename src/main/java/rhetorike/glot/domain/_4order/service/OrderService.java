package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.*;
import rhetorike.glot.global.util.dto.SingleParamDto;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static rhetorike.glot.domain._4order.service.SubscriptionService.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final OrderRepository orderRepository;
    private final PayService payService;
    private final SubscriptionService subscriptionService;
    private final RefundService refundService;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public SingleParamDto<String> makeBasicOrder(OrderDto.BasicOrderRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (subscriptionService.getSubStatus(user) == SubStatus.SUBSCRIBED){
            throw new SubscriptionOngoingException();
        }
        PlanPeriod planPeriod = PlanPeriod.findByName(requestDto.getPlanPeriod());
        Plan plan = planRepository.findBasicByPlanPeriod(planPeriod).orElseThrow(ResourceNotFoundException::new);
        Order order = orderRepository.save(Order.newOrder(user, plan, 1));
        return new SingleParamDto<>(payOrder(order, requestDto.getPayment()));
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public SingleParamDto<String> makeEnterpriseOrder(OrderDto.EnterpriseOrderRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (subscriptionService.getSubStatus(user) == SubStatus.SUBSCRIBED){
            throw new SubscriptionOngoingException();
        }
        PlanPeriod planPeriod = PlanPeriod.findByName(requestDto.getPlanPeriod());
        Plan plan = planRepository.findEnterpriseByPlanPeriod(planPeriod).orElseThrow(ResourceNotFoundException::new);
        Order order = orderRepository.save(Order.newOrder(user, plan, requestDto.getQuantity()));
        return new SingleParamDto<>(payOrder(order, requestDto.getPayment()));
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public String payOrder(Order order, Payment payment) {
        subscriptionService.makeSubscribe(order);
        PortOneResponse.OneTimePay payResponse = payService.pay(order, payment);
        order.setStatus(OrderStatus.findByName(payResponse.getStatus()));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
        return order.getId();
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public String payOrder(Order order) {
        subscriptionService.makeSubscribe(order);
        PortOneResponse.AgainPay payResponse = payService.payAgain(order);
        order.setStatus(OrderStatus.findByName(payResponse.getStatus()));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
        return order.getId();
    }

    private List<OrderDto.History> getOrderHistory(User user) {
        List<OrderDto.History> result = new ArrayList<>();
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);
        List<PortOneResponse.PayHistory> history = payService.getHistory(orders);
        for (int i = 0; i < orders.size(); i++) {
            result.add(OrderDto.History.from(orders.get(i), history.get(i)));
        }
        return result;
    }

    public OrderDto.GetResponse getPayInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        SubStatus subStatus = subscriptionService.getSubStatus(user);
        if (subStatus == SubStatus.FREE){
            return makeFreeResponse();
        }
        Order order = orderRepository.findTop1ByStatusAndUserOrderByCreatedTimeDesc(OrderStatus.PAID, user).get();
        if (subStatus == SubStatus.PAUSED){
            return makePausedResponse(user, order);
        }
        return makeSubsResponse(user, order);
    }

    @NotNull
    private static OrderDto.GetResponse makeFreeResponse() {
        return OrderDto.GetResponse.builder()
                .plan("무료 요금제")
                .status("구독 안함")
                .build();
    }

    @NotNull
    private OrderDto.GetResponse makePausedResponse(User user, Order recentOrder) {
        LocalDate firstOrderedDate = recentOrder.getFirstOrderedDate();
        String payPeriod = recentOrder.getPlan().getPlanPeriod().getDescription() + firstOrderedDate.format(DateTimeFormatter.ofPattern("dd일"));
        return OrderDto.GetResponse.builder()
                .plan(recentOrder.getPlan().getName())
                .status("구독 정지")
                .payPeriod(payPeriod)
                .payMethod(payService.getPayMethod(user).getCardName())
                .nextPayDate(null)
                .firstPaidDate(recentOrder.getFirstOrderedDate())
                .history(getOrderHistory(user))
                .build();
    }

    @NotNull
    private OrderDto.GetResponse makeSubsResponse(User user, Order recentOrder) {
        LocalDate firstOrderedDate = recentOrder.getFirstOrderedDate();
        String payPeriod = recentOrder.getPlan().getPlanPeriod().getDescription() + firstOrderedDate.format(DateTimeFormatter.ofPattern("dd일"));
        return OrderDto.GetResponse.builder()
                .plan(recentOrder.getPlan().getName())
                .status("구독 중")
                .payPeriod(payPeriod)
                .payMethod(payService.getPayMethod(user).getCardName())
                .nextPayDate(recentOrder.getSubscription().getEndDate().plusDays(1L))
                .firstPaidDate(recentOrder.getFirstOrderedDate())
                .history(getOrderHistory(user))
                .build();
    }

    public void changePayMethod(Payment payment, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        payService.changePayMethod(user, payment);
    }

    @Transactional
    public void reorder(LocalDate endDate) {
        List<Subscription> closedSubscriptions = subscriptionRepository.findByContinuedIsTrueAndEndDate(endDate);
        for (Subscription subscription : closedSubscriptions) {
            Order reorder = orderRepository.save(Order.newReorder(subscription.getOrder()));
            payOrder(reorder);
        }
    }

    @Transactional
    public void refund(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByOrderer(user).orElseThrow(ResourceNotFoundException::new);
        long amount = refundService.calcRefundAmount(subscription);
        if (amount == 0L) {
            throw new RefundDeniedException();
        }
        subscriptionService.deleteSubscriptionAndMembers(subscription);
        Order order = subscription.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        payService.refund(subscription.getOrder(), amount);
    }

    public OrderDto.RefundResponse getRefundInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByOrderer(user).orElseThrow(ResourceNotFoundException::new);
        long refundAmount = refundService.calcRefundAmount(subscription);
        return OrderDto.RefundResponse.builder()
                .accountId(user.getAccountId())
                .remainDays(subscription.getRemainDays())
                .numOfMembers(subscription.getMembers().size())
                .refundAmount(refundAmount)
                .build();
    }
}
