package rhetorike.glot.domain._4order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;

    @Mock
    SubscriptionRepository subscriptionRepository;
    @Mock
    UserService userService;
    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("베이직 요금제 구독")
    void subscribeBasic(){
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, PlanPeriod.MONTH);
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now());

        //when
        subscriptionService.makeSubscribe(order);

    }

    @Test
    @DisplayName("엔터프라이즈 요금제 구독")
    void subscribeEnterprise(){
        //given
        User user = Organization.builder().id(1L).build();
        Plan plan = new EnterprisePlan(null, "test", 100, PlanPeriod.MONTH);
        Order order = Order.newOrder(user, plan, 5);
        order.setCreatedTime(LocalDateTime.now());
        given(userService.findOrCreateMember(any())).willReturn(new OrganizationMember());

        //when
        subscriptionService.makeSubscribe(order);
    }


    @Test
    @DisplayName("구독 취소")
    void unsubscribe(){
        //given
        Long userId = 1L;
        User user = Personal.builder().id(userId).build();
        Subscription subscription = Subscription.builder().build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(subscriptionRepository.findByOrderer(user)).willReturn(Optional.of(subscription));

        //when
        subscriptionService.unsubscribe(userId);

        //then
        verify(userRepository).findById(userId);
        verify(subscriptionRepository).findByOrderer(user);
    }


}