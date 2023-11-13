package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
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
    @DisplayName("User가 구독 중인 Subscription을 조회한다.")
    void findByUser(){
        //given
        User user = userRepository.save(new Personal());
        Order order = orderRepository.save(Order.builder().id("id").user(user).build());
        Subscription subscription = subscriptionRepository.save(Subscription.newSubscription(LocalDate.now(), LocalDate.now()));
        order.setSubscription(subscription);

        //when
        Optional<Subscription> found = subscriptionRepository.findByUser(user);

        //then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(subscription);
    }
}