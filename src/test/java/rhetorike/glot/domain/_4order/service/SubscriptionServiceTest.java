package rhetorike.glot.domain._4order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("베이직 요금제 구독")
    void subscribeBasic() {
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
    void subscribeEnterprise() {
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
    void unsubscribe() {
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


    @Test
    @DisplayName("[구독 계정 조회]")
    void getSubscriptionMembers() {
        //given
        Long userId = 1L;
        User user = Organization.builder().id(userId).build();
        List<User> members = List.of(OrganizationMember.newOrganizationMember("1", "1"), OrganizationMember.newOrganizationMember("2", "2"));
        members.forEach(u -> u.updateLoginLog(LocalDateTime.of(2023, 11, 1, 1, 1, 1)));
        Subscription subscription = Subscription.builder().members(members).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(subscriptionRepository.findByOrderer(user)).willReturn(Optional.of(subscription));

        //when
        List<SubscriptionDto.MemberResponse> result = subscriptionService.getSubscriptionMembers(userId);

        //then
        verify(userRepository).findById(userId);
        verify(subscriptionRepository).findByOrderer(user);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAccountId()).isEqualTo("1");
        assertThat(result.get(0).getName()).isNull();
        assertThat(result.get(0).getLastLog()).isEqualTo(LocalDateTime.of(2023, 11, 1, 1, 1, 1));
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("[구독 계정 정보 수정]")
    void updateSubscriptionMembers() {
        //given
        Long userId = 1L;
        User manager = Organization.builder().id(userId).build();
        User member = OrganizationMember.newOrganizationMember("aaa123", "aaa123");
        Subscription subscription = Subscription.builder().members(List.of(member)).build();
        SubscriptionDto.MemberUpdateRequest requestDto = new SubscriptionDto.MemberUpdateRequest("aaa123", "updatedPassword", null, null);
        given(userRepository.findById(userId)).willReturn(Optional.of(manager));
        given(userRepository.findByAccountId("aaa123")).willReturn(Optional.of(member));
        given(subscriptionRepository.findByOrderer(manager)).willReturn(Optional.of(subscription));

        //when
        subscriptionService.updateSubscriptionMembers(userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(userRepository).findByAccountId("aaa123");
        verify(subscriptionRepository).findByOrderer(manager);
        assertThat(member.getName()).isNull();
        assertThat(member.isActive()).isTrue();
    }
}