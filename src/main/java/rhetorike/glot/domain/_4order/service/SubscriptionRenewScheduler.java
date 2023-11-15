package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRenewScheduler {

    private static final long FIXED_ONE_DAY = 1000L * 60 * 60 * 24;
    private static final long FIXED_TEN_YEAR = 1000L * 60 * 60 * 24 * 365 * 10;
    private static final long FIXED_ONE_MINUTE = 1000L * 60;
    private static final String CRON_MIDNIGHT = "0 58 23 * * *";
    private static final String CRON_DAYBREAK = "0 59 4 * * *";
    private final OrderService orderService;
    private final SubscriptionService subscriptionService;

    //    @Scheduled(fixedRate = FIXED_ONE_MINUTE)
    @Scheduled(cron = CRON_MIDNIGHT)
    public void renew() {
        log.info("시작");
        orderService.reorder(LocalDate.now());
        subscriptionService.deleteOverdue(LocalDate.now());
        log.info("종료");
    }
}
