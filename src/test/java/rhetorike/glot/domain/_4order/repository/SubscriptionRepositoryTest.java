package rhetorike.glot.domain._4order.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.setup.RepositoryTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
class SubscriptionRepositoryTest {
    @Autowired
    SubscriptionRepository subscriptionRepository;

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
    @DisplayName("Subscription을 저장할 때, 멤버를 함께 저장한다.")
    void saveWithUser(){
        //given
        Organization organization = Organization.builder().build();
        List<User> members = organization.generateMembers(5);
        Subscription subscription = Subscription.newSubscription(LocalDate.now(), members);
        Subscription saved = subscriptionRepository.save(subscription);

        //when
        Optional<Subscription> found = subscriptionRepository.findById(saved.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get().getMembers()).hasSize(5);
        assertThat(found.get().getMembers().get(0).getId()).isNotNull();
    }
}