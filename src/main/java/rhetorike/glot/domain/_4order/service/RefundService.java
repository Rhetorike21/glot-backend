package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final WritingBoardService writingBoardService;

    public long calcRefundAmount(Subscription subscription) {
        PlanPeriod planPeriod = subscription.getOrder().getPlan().getPlanPeriod();
        if (planPeriod == PlanPeriod.MONTH) {
            return monthlyRefundMethod(subscription);
        }
        return yearlyRefundMethod(subscription);
    }

    private long monthlyRefundMethod(Subscription subscription) {
        long totalPriceWithoutDiscount = subscription.getOrder().calcTotalPriceWithoutDiscount();
        long totalPrice = subscription.getOrder().getTotalPrice();
        LocalDate startDate = subscription.getStartDate();
        int pastDays = Period.between(startDate, LocalDate.now()).getDays();
        if (pastDays < 7 && !writingBoardService.hasUsedBoard(subscription)) {
            return totalPrice;
        }
        if (pastDays < 14) {
            return totalPrice - (totalPriceWithoutDiscount / 31) * pastDays;
        }
        return 0L;
    }

    private long yearlyRefundMethod(Subscription subscription) {
        long totalPriceWithoutDiscount = subscription.getOrder().calcTotalPriceWithoutDiscount();
        long totalPrice = subscription.getOrder().getTotalPrice();
        LocalDate startDate = subscription.getStartDate();
        int pastDays = Period.between(startDate, LocalDate.now()).getDays();
        if (pastDays < 7 && !writingBoardService.hasUsedBoard(subscription)) {
            return totalPrice;
        }
        if (pastDays < 14) {
            return totalPrice - (totalPriceWithoutDiscount / 365) * pastDays;
        }
        return 0L;
    }


}