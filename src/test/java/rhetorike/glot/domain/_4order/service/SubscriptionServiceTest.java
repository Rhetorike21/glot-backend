package rhetorike.glot.domain._4order.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.OrderNotCompletedException;
import rhetorike.glot.setup.ServiceTest;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ServiceTest
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;

    @Mock
    SubscriptionRepository subscriptionRepository;


    @Test
    @DisplayName("베이직 요금제 구매")
    void orderBasic(){
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);

        //when
        subscriptionService.subscribe(order);

        //then
        assertThat(user.getSubscription()).isNotNull();
    }

    @Test
    @DisplayName("엔터프라이즈 요금제 구매")
    void orderEnterprise(){
        //given
        User user = Organization.builder().id(1L).build();
        Plan plan = new EnterprisePlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 5);

        //when
        subscriptionService.subscribe(order);

        //then
        Subscription subscription = user.getSubscription();
        assertThat(subscription).isNotNull();
        assertThat(subscription.getMembers()).hasSize(6);
    }

    @Test
    @DisplayName("엔터프라이즈 요금제 구매 - 일반 사용자가 구매하는 경우, 예외 발생")
    void personalCanNotOrderEntPlan(){
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new EnterprisePlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);

        //then
        assertThatThrownBy(() -> subscriptionService.subscribe(order)).isInstanceOf(ClassCastException.class);
    }
}