package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    public enum SubStatus{
        SUBSCRIBED,
        PAUSED,
        FREE
    }

    public SubStatus getSubStatus(User user){
        Optional<Order> orderOptional = orderRepository.findTop1ByStatusAndUserOrderByCreatedTimeDesc(OrderStatus.PAID, user);
        if (orderOptional.isEmpty()) {
            return SubStatus.FREE;
        }
        Order order = orderOptional.get();
        if (order.getSubscription() == null) {
            return SubStatus.PAUSED;
        }
        return SubStatus.SUBSCRIBED;
    }

    public void makeSubscribe(Order order) {
        Subscription subscription = subscriptionRepository.save(Subscription.newSubscription(order));
        List<User> members = initMembers(order);
        for (User member : members) {
            member.setSubscription(subscription);
        }
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

    @Transactional
    public void pauseOverdueSubscriptions(LocalDate endDate) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByEndDate(endDate);
        for (Subscription subscription : subscriptions) {
            deleteSubscriptionOnly(subscription);
        }
    }
    private void deleteSubscriptionOnly(Subscription subscription) {
        List<User> members = userRepository.findBySubscription(subscription);
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setSubscription(null);
        }
        freeOrder(subscription.getOrder());
        subscriptionRepository.delete(subscription);
    }

    public void deleteSubscriptionAndMembers(Subscription subscription) {
        deleteAllMembers(subscription);
        freeOrder(subscription.getOrder());
        subscriptionRepository.delete(subscription);
    }
    private void deleteAllMembers(Subscription subscription){
        List<OrganizationMember> members = userRepository.findMemberBySubscription(subscription);
        userRepository.deleteAll(members);
    }




    private void freeOrder(Order order) {
        order.setSubscription(null);
        order.getUser().setSubscription(null);
    }

    @Transactional
    public void unsubscribe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByOrderer(user).orElseThrow(ResourceNotFoundException::new);
        subscription.unsubscribe();
    }

    public List<SubscriptionDto.MemberResponse> getSubscriptionMembers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Subscription subscription = subscriptionRepository.findByOrderer(user).orElseThrow(ResourceNotFoundException::new);
        return subscription.getMembers().stream()
                .map(SubscriptionDto.MemberResponse::new)
                .toList();
    }

    @Transactional
    public void updateSubscriptionMembers(Long managerId, SubscriptionDto.MemberUpdateRequest requestDto) {
        User manager = userRepository.findById(managerId).orElseThrow(UserNotFoundException::new);
        validateOrganization(manager);
        Subscription subscription = subscriptionRepository.findByOrderer(manager).orElseThrow(ResourceNotFoundException::new);
        User member = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        if (subscription.equals(member.getSubscription())) {
            member.update(requestDto.toUpdateParam(), passwordEncoder);
            member.updateActive(requestDto.getActive());
        }
    }

    private void validateOrganization(User manager) {
        if (manager instanceof Organization) {
            return;
        }
        throw new AccessDeniedException();
    }
}
