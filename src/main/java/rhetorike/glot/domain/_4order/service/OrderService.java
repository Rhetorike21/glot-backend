package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final OrderRepository orderRepository;
    private final PayService payService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public void makeOrder(OrderDto.MakeRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Plan plan = planRepository.findById(requestDto.getPlanId()).orElseThrow(ResourceNotFoundException::new);
        Order order = Order.newOrder(user, plan, requestDto.getQuantity());
        payService.pay(order, requestDto.getPayment());
        subscriptionService.subscribe(order);
        orderRepository.save(order);
    }

    public List<OrderDto.GetResponse> getOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);
        return payService.getHistory(orders);
    }
}
