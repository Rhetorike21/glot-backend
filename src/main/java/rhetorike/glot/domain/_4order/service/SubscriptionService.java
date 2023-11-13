package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Subscription makeSubscribe(Order order) {
        LocalDate startDate = order.getCreatedTime().toLocalDate();
        LocalDate endDate = order.getPlan().endDateFrom(startDate);
        Subscription subscription = subscriptionRepository.save(Subscription.newSubscription(startDate, endDate));
        List<User> members = initMembers(order);
        setSubscriptionToMembers(members, subscription);
        return subscription;
    }

    public List<User> initMembers(Order order) {
        User user = order.getUser();
        Plan plan = order.getPlan();
        List<User> members = new ArrayList<>();

        if (plan instanceof BasicPlan) {
            members.add(user);
            return members;
        }
        if (plan instanceof EnterprisePlan) {
            for (int i = 1; i <= order.getQuantity(); i++) {
                String accountId = user.generateEnterpriseName() + String.format("%05d", i);
                User member = userService.findOrCreateMember(accountId);
                members.add(member);
            }
            return members;
        }
        throw new AccessDeniedException();
    }
    private void setSubscriptionToMembers(List<User> members, Subscription subscription) {
        for (User member : members) {
            member.setSubscription(subscription);
        }
    }

    @Transactional
    public void deleteOverdue(LocalDate endDate) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByEndDate(endDate);
        for (Subscription subscription : subscriptions) {
            deleteSubscription(subscription);
        }
    }
    private void deleteSubscription(Subscription subscription){
        freeMember(subscription);
        freeOrder(subscription.getOrder());
        subscriptionRepository.delete(subscription);
    }
    private void freeMember(Subscription subscription) {
        List<User> members = userRepository.findBySubscription(subscription);
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setSubscription(null);
        }
    }
    private void freeOrder(Order order){
        order.setSubscription(null);
        order.getUser().setSubscription(null);
    }

    @Transactional
    public void unsubscribe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByUser(user).orElseThrow(ResourceNotFoundException::new);
        subscription.unsubscribe();
    }
}
