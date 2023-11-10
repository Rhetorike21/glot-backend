package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.PaymentFailedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.dto.SingleParamDto;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final OrderRepository orderRepository;
    private final PayService payService;
    private final SubscriptionService subscriptionService;

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

    public String payOrder(Order order, Payment payment) {
        PortOneResponse.OneTimePay payResponse = payService.pay(order, payment);
        OrderStatus status = OrderStatus.findByName(payResponse.getStatus());
        order.setStatus(status);

        Order payedOrder = orderRepository.save(order);
        if (status != OrderStatus.PAID) {
            throw new PaymentFailedException(payResponse.getFail_reason());
        }
        Subscription subscription = subscriptionService.makeSubscribe(payedOrder);
        payedOrder.setSubscription(subscription);
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
}
