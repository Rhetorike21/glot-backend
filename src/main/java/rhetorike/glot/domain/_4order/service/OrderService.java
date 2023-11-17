package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.Organization;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        PlanPeriod planPeriod = PlanPeriod.findByName(requestDto.getPlanPeriod());
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Plan plan = planRepository.findBasicByPlanPeriod(planPeriod).orElseThrow(ResourceNotFoundException::new);
        String orderId = payOrder(Order.newOrder(user, plan, 1), requestDto.getPayment());
        return new SingleParamDto<>(orderId);
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public SingleParamDto<String> makeEnterpriseOrder(OrderDto.EnterpriseOrderRequest requestDto, Long userId) {
        PlanPeriod planPeriod = PlanPeriod.findByName(requestDto.getPlanPeriod());
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user instanceof Organization) {
            Plan plan = planRepository.findEnterpriseByPlanPeriod(planPeriod).orElseThrow(ResourceNotFoundException::new);
            String orderId = payOrder(Order.newOrder(user, plan, requestDto.getQuantity()), requestDto.getPayment());
            return new SingleParamDto<>(orderId);
        }
        throw new AccessDeniedException();
    }

    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public String payOrder(Order order, Payment payment) {
        PortOneResponse.OneTimePay payResponse = payService.pay(order, payment);
        OrderStatus status = OrderStatus.findByName(payResponse.getStatus());
        order.setStatus(status);

        Order payedOrder = orderRepository.save(order);
        if (status != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
        subscriptionService.makeSubscribe(payedOrder);
        return order.getId();
    }
    @Transactional(dontRollbackOn = PaymentFailedException.class)
    public String payOrder(Order order) {
        PortOneResponse.AgainPay payResponse = payService.payAgain(order);
        OrderStatus status = OrderStatus.findByName(payResponse.getStatus());
        order.setStatus(status);

        Order payedOrder = orderRepository.save(order);
        if (status != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFailReason());
        }
        subscriptionService.makeSubscribe(payedOrder);
        return payedOrder.getId();
    }

    public List<OrderDto.GetResponse> getOrders(Long userId) {
        List<OrderDto.GetResponse> result = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);
        List<PortOneResponse.PayHistory> history = payService.getHistory(orders);
        for (int i = 0; i < orders.size(); i++) {
            result.add(OrderDto.GetResponse.from(orders.get(i), history.get(i)));
        }
        return result;
    }

    public void changePayMethod(Payment payment, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        payService.changePayMethod(user, payment);
    }

    @Transactional
    public void reorder(LocalDate endDate) {
        List<Subscription> closedSubscriptions = subscriptionRepository.findByContinuedIsTrueAndEndDate(endDate);
        for (Subscription subscription : closedSubscriptions) {
            payOrder(Order.newReorder(subscription.getOrder()));
        }
    }

    @Transactional
    public void refund(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByOrderer(user).orElseThrow(ResourceNotFoundException::new);
        long amount = refundService.calcRefundAmount(subscription);
        if (amount == 0L){
            throw new RefundDeniedException();
        }
        payService.refund(subscription.getOrder(), amount);
        subscription.getOrder().setStatus(OrderStatus.CANCELLED);
        subscriptionService.deleteSubscription(subscription);
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
