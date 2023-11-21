package rhetorike.glot.domain._4order.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Slf4j
@ServiceTest
class RefundServiceTest {
    @InjectMocks
    RefundService refundService;

    @Mock
    WritingBoardService writingBoardService;

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6"})
    @DisplayName("[월간 요금제] 결제일로부터 0~6일 경과한 시점에서 작문 기록이 없는 경우, 전액 환불")
    void refundMonthlyAll(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(30000L).discountedPrice(18000L).expiryPeriod(PlanPeriod.MONTH).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);
        given(writingBoardService.hasUsedBoard(subscription)).willReturn(false);

        //when
        long amount = refundService.calcRefundAmount(subscription);

        //then
        assertThat(amount).isEqualTo(order.getTotalPrice());
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6"})
    @DisplayName("[월간 요금제] 결제일로부터 0~6일 경과한 시점에서 작문 기록이 있는 경우, 차감")
    void refundMonthlyPartial1to6(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(30000L).discountedPrice(18000L).expiryPeriod(PlanPeriod.MONTH).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);
        given(writingBoardService.hasUsedBoard(subscription)).willReturn(true);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isLessThan(order.getTotalPrice());
    }


    @ParameterizedTest
    @CsvSource({"7", "8", "9", "10", "11", "12", "13"})
    @DisplayName("[월간 요금제] 결제일로부터 7~13일 경과한 시점에서 작문 기록이 있는 경우, 차감")
    void refundMonthlyPartial7to13(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(30000L).discountedPrice(18000L).expiryPeriod(PlanPeriod.MONTH).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isLessThan(order.getTotalPrice());
    }

    @ParameterizedTest
    @CsvSource({"14", "15"})
    @DisplayName("[월간 요금제] 결제일로부터 14일 이상 경과한 시점에서 환불 불가")
    void refundMonthlyDenied(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(30000L).discountedPrice(18000L).expiryPeriod(PlanPeriod.MONTH).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isEqualTo(0L);
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6"})
    @DisplayName("[연간 요금제] 결제일로부터 0~6일 경과한 시점에서 작문 기록이 없는 경우, 전액 환불")
    void refundYearlyAll(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(20000L).discountedPrice(14400L).expiryPeriod(PlanPeriod.YEAR).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);
        given(writingBoardService.hasUsedBoard(subscription)).willReturn(false);

        //when
        long amount = refundService.calcRefundAmount(subscription);

        //then
        assertThat(amount).isEqualTo(order.getTotalPrice());
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6"})
    @DisplayName("[연간 요금제] 결제일로부터 0~6일 경과한 시점에서 작문 기록이 있는 경우, 차감")
    void refundYearlyPartial1to6(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(20000L).discountedPrice(14400L).expiryPeriod(PlanPeriod.YEAR).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);
        given(writingBoardService.hasUsedBoard(subscription)).willReturn(true);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isLessThan(order.getTotalPrice());
    }


    @ParameterizedTest
    @CsvSource({"7", "8", "9", "10", "11", "12", "13"})
    @DisplayName("[연간 요금제] 결제일로부터 7~13일 경과한 시점에서 작문 기록이 있는 경우, 차감")
    void refundYearlyPartial7to13(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(20000L).discountedPrice(14400L).expiryPeriod(PlanPeriod.YEAR).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isLessThan(order.getTotalPrice());
    }

    @ParameterizedTest
    @CsvSource({"14", "15"})
    @DisplayName("[연간 요금제] 결제일로부터 14일 이상 경과한 시점에서 환불 불가")
    void refundYearlyDenied(int minusDay) {
        //given
        User user = new Personal();
        Plan plan = BasicPlan.builder().price(20000L).discountedPrice(14400L).expiryPeriod(PlanPeriod.YEAR).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now().minusDays(minusDay));
        Subscription subscription = Subscription.newSubscription(order);

        //when
        long amount = refundService.calcRefundAmount(subscription);
        log.info("{}", amount);

        //then
        assertThat(amount).isEqualTo(0L);
    }

    @Test
    @DisplayName("두 LocalDate 사이의 시간을 구한다.")
    void betweenLocalDate() {
        //given
        LocalDate date1 = LocalDate.of(2023, 11, 15);
        LocalDate date2 = LocalDate.of(2023, 11, 23);
        Period period1 = Period.between(date1, date2);
        Period period2 = Period.between(date2, date1);

        //when

        //then
        assertThat(period1).isEqualTo(Period.ofDays(8));
        assertThat(period2).isEqualTo(Period.ofDays(-8));
    }


    @Test
    @DisplayName("일의 자리에서 반올림")
    void round() {
        //given
        long num = 12345;

        //when
        long result = Math.round(num * 0.1) * 10;

        //then
        assertThat(result).isEqualTo(12350);
    }


}