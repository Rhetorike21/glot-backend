package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.setup.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Test
    @DisplayName("사용자의 구매 내역을 최근 순으로 조회한다. ")
    void findByUser() {
        //given
        User user = userRepository.save(Personal.builder().build());
        Plan plan = planRepository.save(new BasicPlan());
        Order order1 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order2 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order3 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order4 = orderRepository.save(Order.newOrder(user, plan, 1));

        //when
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);

        //then
        assertThat(orders).containsExactly(order4, order3, order2, order1);
    }


    @Test
    @DisplayName("가장 최근의 주문을 조회한다.")
    void findRecentestOrder(){
        //given
        User user = userRepository.save(Personal.builder().build());
        Plan plan = planRepository.save(new BasicPlan());
        Order order1 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order2 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order3 = orderRepository.save(Order.newOrder(user, plan, 1));
        order1.setStatus(OrderStatus.PAID);
        order2.setStatus(OrderStatus.PAID);
        order3.setStatus(OrderStatus.FAILED);

        //when
        Optional<Order> found = orderRepository.findTop1ByStatusAndUserOrderByCreatedTimeDesc(OrderStatus.PAID, user);

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(order2);
    }


}