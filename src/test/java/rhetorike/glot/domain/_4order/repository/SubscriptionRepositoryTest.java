package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.setup.RepositoryTest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Plan plan = EnterprisePlan.builder().planPeriod(PlanPeriod.MONTH).build();
        Subscription subscription = Subscription.newSubscription(LocalDate.now(), plan.endDateFrom(LocalDate.now()));

        List<User> members = List.of(OrganizationMember.newOrganizationMember("accountId1", "password1"), OrganizationMember.newOrganizationMember("accountId2", "password2"));
        for (User member : members) {
            member.setSubscription(subscription);
        }
        subscription.setMembers(members);
        Subscription saved = subscriptionRepository.save(subscription);

        //when
        Optional<Subscription> found = subscriptionRepository.findById(saved.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get().getMembers()).hasSize(2);
        assertThat(found.get().getMembers().get(0).getId()).isNotNull();
    }
}