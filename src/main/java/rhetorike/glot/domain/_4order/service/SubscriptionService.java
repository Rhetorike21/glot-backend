package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.OrderNotCompletedException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void subscribe(Order order) {
        Subscription subscription = order.subscribe();
        subscriptionRepository.save(subscription);
        User manager = order.getUser();
        manager.setSubscription(subscription);
    }
}
