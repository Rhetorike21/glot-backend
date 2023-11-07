package rhetorike.glot.global.util.portone;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.setup.IntegrationTest;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("secret")
@Disabled
class PortOneClientTest extends IntegrationTest {

    @Value("${pay.card}")
    private String cardNumber;
    @Value("${pay.expiry}")
    private String expiry;
    @Value("${pay.birth}")
    private String birth;
    @Value("${pay.password}")
    private String password;

    @Autowired
    PortOneClient portOneClient;

    @Test
    @DisplayName("[액세스 토큰 발급]")
    void getAccessToken() {
        //given

        //when
        String result = portOneClient.issueToken().getAccessToken();
        log.info(result);

        //then
        assertThat(result).isNotEmpty();
    }


    @Test
    @DisplayName("[최초 결제 및 키 발급]")
    void payAndSaveBillingKey() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);

        //when
        PortOneResponse.OneTimePay response = portOneClient.payAndSaveBillingKey(order, payment);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
        assertThat(response.getCustomer_uid()).isNotEmpty();
    }

    @Test
    @DisplayName("[빌링키 발급]")
    void issueBillingKey() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        portOneClient.payAndSaveBillingKey(order, payment);

        //when
        PortOneResponse.IssueBillingKey response = portOneClient.issueBillingKey(user.getId(), payment);
        log.info("{}", response);

        //then
    }

    @Test
    @DisplayName("[빌링키 제거]")
    void deleteBillingKey() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        portOneClient.payAndSaveBillingKey(order, payment);

        //when
        PortOneResponse.DeleteBillingKey response = portOneClient.deleteBillingKey(user.getId());
        log.info("{}", response);

        //then
    }


    @Test
    @DisplayName("[결제 내역 조회]")
    void getPaymentsHistory() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        PortOneResponse.OneTimePay response = portOneClient.payAndSaveBillingKey(order, payment);
        String impUid = response.getImpUid();

        //when
        PortOneResponse.PayHistory historyResponse = portOneClient.getPaymentsHistory(impUid);
        log.info("{}", historyResponse);

        //then
        assertThat(historyResponse.getImpUid()).isNotEmpty();
    }

    @Test
    @DisplayName("[재결제]")
    void payAgain() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order1 = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        portOneClient.payAndSaveBillingKey(order1, payment);

        //when
        Order order2 = Order.newOrder(user, plan, 1);
        PortOneResponse.AgainPay response = portOneClient.payAgain(order2);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }

    @Test
    @DisplayName("[환불]")
    void cancel() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        PortOneResponse.OneTimePay oneTimePayResponse = portOneClient.payAndSaveBillingKey(order, payment);

        //when
        PortOneResponse.Cancel response = portOneClient.cancel(oneTimePayResponse.getImpUid(), null);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }

    @Test
    @Disabled
    @DisplayName("[부분 환불] : 테스트 MID 아닌 경우에만 가능")
    void cancelPartially() {
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment(cardNumber, expiry, birth, password);
        PortOneResponse.OneTimePay oneTimePayResponse = portOneClient.payAndSaveBillingKey(order, payment);

        //when
        PortOneResponse.Cancel response = portOneClient.cancel(oneTimePayResponse.getImpUid(), "50");
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }
}