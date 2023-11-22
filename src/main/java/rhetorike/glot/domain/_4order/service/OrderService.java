package rhetorike.glot.domain._4order.service;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.function.Predicate;

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
    public SingleParamDto<String> makeOrder(OrderDto.MakeRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (subscriptionService.getSubStatus(user) == SubStatus.SUBSCRIBED){
            throw new SubscriptionOngoingException();
        }
        Plan plan = planRepository.findById(requestDto.getPlanId()).orElseThrow(ResourceNotFoundException::new);
        Order order = orderRepository.save(Order.newOrder(user, plan, requestDto.getQuantity()));
        subscriptionService.makeSubscription(order);
        payOrder(order, requestDto.getPayment());
        return new SingleParamDto<>(order.getId());
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public void payOrder(Order order, Payment payment) {
        PortOneResponse.OneTimePay payResponse = payService.pay(order, payment);
        order.setStatus(OrderStatus.findByName(payResponse.getStatus()));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public void payOrder(Order order) {
        PortOneResponse.AgainPay payResponse = payService.payAgain(order);
        order.setStatus(OrderStatus.findByName(payResponse.getStatus()));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
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
        String payPeriod = recentOrder.getFirstOrderedDate().format(DateTimeFormatter.ofPattern("dd"));
        String firstPaidDate = recentOrder.getFirstOrderedDate().format(DateTimeFormatter.ofPattern("yyyy.MM월"));
        return OrderDto.GetResponse.builder()
                .plan(recentOrder.getPlan().getName())
                .status("구독 정지")
                .payPeriod(payPeriod)
                .payMethod(payService.getPayMethod(user).getCardName())
                .nextPayDate(null)
                .firstPaidDate(firstPaidDate)
                .history(getOrderHistory(user))
                .build();
    }

    @NotNull
    private OrderDto.GetResponse makeSubsResponse(User user, Order recentOrder) {
        String payPeriod = recentOrder.getFirstOrderedDate().format(DateTimeFormatter.ofPattern("dd"));
        String firstPaidDate = recentOrder.getFirstOrderedDate().format(DateTimeFormatter.ofPattern("yyyy.MM월"));
        String nextPayDate = recentOrder.getFirstOrderedDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (EEE)"));

        return OrderDto.GetResponse.builder()
                .plan(recentOrder.getPlan().getName())
                .status("구독 중")
                .payPeriod(payPeriod)
                .payMethod(payService.getPayMethod(user).getCardName())
                .nextPayDate(nextPayDate)
                .firstPaidDate(firstPaidDate)
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
            List<User> members = subscription.getMembers();
            long numOfInvalid = members.stream().filter(Predicate.not(User::isActive)).count();
            if (members.size() != numOfInvalid) {
                Order reorder = orderRepository.save(Order.newReorder(subscription.getOrder(), numOfInvalid));
                subscriptionService.renewSubscription(reorder, subscription);
                payOrder(reorder);
            }
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
