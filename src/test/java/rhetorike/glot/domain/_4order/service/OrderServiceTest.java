package rhetorike.glot.domain._4order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.setup.ServiceTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PlanRepository planRepository;
    @Mock
    PayService payService;
    @Mock
    SubscriptionService subscriptionService;

    @Test
    @DisplayName("[상품 주문]")
    void makeOrder(){
        //given
        Long itemId = 1L;
        Long userId = 1L;
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        User user = Personal.builder().build();
        Plan plan = new BasicPlan();
        given(planRepository.findById(itemId)).willReturn(Optional.of(plan));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        orderService.makeOrder(new OrderDto.MakeRequest(itemId, 1, payment), userId);

        //then
        verify(planRepository).findById(itemId);
        verify(userRepository).findById(userId);
    }


    @Test
    @DisplayName("[주문 내역 조회]")
    void getOrders(){
        //given
        Long userId = 1L;
        User user = Personal.builder().build();
        Plan plan = new BasicPlan();
        Order order = Order.newOrder(user, plan, 1);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.findByUserOrderByCreatedTimeDesc(user)).willReturn(List.of(order));

        //when
        orderService.getOrders(userId);

        //then
        verify(userRepository).findById(userId);
        verify(orderRepository).findByUserOrderByCreatedTimeDesc(user);
    }

}