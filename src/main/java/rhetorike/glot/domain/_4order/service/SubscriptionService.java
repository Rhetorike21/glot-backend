package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    @Transactional
    public Subscription makeSubscribe(Order order) {
        LocalDate startDate = order.getCreatedTime().toLocalDate();
        LocalDate endDate = order.getPlan().endDateFrom(startDate);
        Subscription subscription = Subscription.newSubscription(startDate, endDate);
        List<User> members = initMembers(order, subscription);
        setSubscriptionToMembers(members, subscription);
        return subscriptionRepository.save(subscription);
    }

    private List<User> initMembers(Order order, Subscription subscription) {
        User user = order.getUser();
        List<User> members = new ArrayList<>();
        if (user instanceof Organization manager) {
            for (int i = 1; i <= order.getQuantity(); i++) {
                String accountId = manager.getOrganizationName() + String.format("%05d", i);
                User member = userService.generateOrganizationMember(accountId);
                members.add(member);
            }
            return members;
        }
        if (user instanceof Personal manager) {
            members.add(manager);
            return members;
        }
        throw new AccessDeniedException();
    }


    private void setSubscriptionToMembers(List<User> members, Subscription subscription) {
        for (User member : members) {
            member.setSubscription(subscription);
        }
        subscription.setMembers(members);
    }
}
