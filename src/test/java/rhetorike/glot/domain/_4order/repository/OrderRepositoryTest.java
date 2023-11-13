package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.setup.RepositoryTest;

import java.time.LocalDate;
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
    @DisplayName("구매 내역을 저장할 때, 구독도 함께 저장된다.")
    void saveSubscriptionAlso() {
        //given
        User user = userRepository.save(Personal.builder().build());
        Plan plan = planRepository.save(new BasicPlan());
        Order order = Order.newOrder(user, plan, 1);
        order.setSubscription(Subscription.newSubscription(LocalDate.now(), LocalDate.now()));
        Order saved = orderRepository.save(order);

        //when
        Optional<Order> found = orderRepository.findById(saved.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get().getSubscription()).isNotNull();
    }

    @Test
    @DisplayName("구독 종료일이 오늘인 구독을 모두 조회한다.")
    void test() {
        //given
        User user = userRepository.save(Personal.builder().build());
        Plan plan = planRepository.save(new BasicPlan());
        Subscription sub1 = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now().minusMonths(1), LocalDate.now().minusDays(2)));
        Subscription sub2 = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now().minusMonths(1), LocalDate.now().minusDays(1)));
        Subscription sub3 = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now().minusMonths(1), LocalDate.now()));
        Subscription sub4 = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now().minusMonths(1), LocalDate.now().plusDays(1)));
        Subscription sub5 = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now().minusMonths(1), LocalDate.now().plusDays(2)));
        Order order1 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order2 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order3 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order4 = orderRepository.save(Order.newOrder(user, plan, 1));
        Order order5 = orderRepository.save(Order.newOrder(user, plan, 1));
        order1.setSubscription(sub1);
        order2.setSubscription(sub2);
        order3.setSubscription(sub3);
        order4.setSubscription(sub4);
        order5.setSubscription(sub5);

        //when
        List<Order> orders = orderRepository.findAllByContinuedAndEndDate(true, LocalDate.now());

        //then
        assertThat(orders).containsExactly(order3);
    }
}