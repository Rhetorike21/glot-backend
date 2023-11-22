package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefundService {
    private final WritingBoardService writingBoardService;

    public long calcRefundAmount(Subscription subscription) {
        Order order = subscription.getOrder();
        int pastDays = Period.between(subscription.getStartDate(), LocalDate.now()).getDays();
        if (pastDays < 7 && !writingBoardService.hasUsedBoard(subscription)) {
            return refundAll(order);
        }
        if (pastDays < 14) {
            return refundPartOfPrice(order, pastDays);
        }
        return 0L;
    }
    private long refundAll(Order order) {
        long result = order.getTotalPrice();
        return roundToTen(result);
    }
    private long refundPartOfPrice(Order order, int pastDays) {
        PlanPeriod planPeriod = order.getPlan().getPlanPeriod();
        long totalAmount = order.getTotalPrice();
        long usedAmount = order.calcTotalPriceWithoutDiscount() / planPeriod.getUnit() * pastDays;
        long vat = order.getVat();
        log.info("{}", totalAmount);
        log.info("{}", usedAmount);
        log.info("{}", vat);
        long result = totalAmount - usedAmount - vat;
        return roundToTen(result);
    }
    private long roundToTen(long num){
        return Math.round(num * 0.1) * 10;
    }
}