package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.setup.RepositoryTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class SubscriptionRepositoryTest {
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("Subscription을 저장하고 조회한다.")
    void saveAndFind(){
        //given
        Subscription saved = subscriptionRepository.save(new Subscription());

        //when
        Optional<Subscription> found = subscriptionRepository.findById(saved.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("구독 종료일이 오늘인 구독을 모두 조회한다.")
    void findByEndDate() {
        //given
        Subscription sub1 = subscriptionRepository.save(Subscription.builder().continued(true).endDate(LocalDate.now().minusDays(1)).build());
        Subscription sub2 = subscriptionRepository.save(Subscription.builder().continued(false).endDate(LocalDate.now()).build());
        Subscription sub3 = subscriptionRepository.save(Subscription.builder().continued(true).endDate(LocalDate.now()).build());
        Subscription sub4 = subscriptionRepository.save(Subscription.builder().continued(true).endDate(LocalDate.now().plusMonths(1)).build());
        Subscription sub5 = subscriptionRepository.save(Subscription.builder().continued(true).endDate(LocalDate.now().plusDays(1)).build());

        //when
        List<Subscription> subscriptions = subscriptionRepository.findByContinuedIsTrueAndEndDate(LocalDate.now());

        //then
        assertThat(subscriptions).containsExactly(sub3);
    }
}