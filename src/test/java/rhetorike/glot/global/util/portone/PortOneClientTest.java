package rhetorike.glot.global.util.portone;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.setup.IntegrationTest;

import java.util.UUID;

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
        String customerUid = UUID.randomUUID().toString();
        String merchantUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";

        //when
        PortOneResponse.OneTimePay response = portOneClient.payAndSaveBillingKey(merchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
        assertThat(response.getCustomer_uid()).isNotEmpty();
    }

    @Test
    @DisplayName("[빌링키 발급]")
    void issueBillingKey() {
        //given
        String merchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        portOneClient.payAndSaveBillingKey(merchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);

        //when
        PortOneResponse.IssueBillingKey response = portOneClient.issueBillingKey(customerUid, cardNumber, expiry, birth, password);
        log.info("{}", response);

        //then
    }

    @Test
    @DisplayName("[빌링키 제거]")
    void deleteBillingKey() {
        //given
        String merchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        portOneClient.payAndSaveBillingKey(merchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);

        //when
        PortOneResponse.DeleteBillingKey response = portOneClient.deleteBillingKey(customerUid);
        log.info("{}", response);

        //then
    }


    @Test
    @DisplayName("[결제 내역 조회]")
    void getPaymentsHistory() {
        //given
        String merchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        PortOneResponse.OneTimePay response = portOneClient.payAndSaveBillingKey(merchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);
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
        String oldMerchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        portOneClient.payAndSaveBillingKey(oldMerchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);

        //when
        String newMerchantUid = UUID.randomUUID().toString();
        PortOneResponse.AgainPay response = portOneClient.payAgain(newMerchantUid, customerUid, name, amount);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }

    @Test
    @DisplayName("[환불]")
    void cancel() {
        //given
        String oldMerchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        PortOneResponse.OneTimePay oneTimePayResponse = portOneClient.payAndSaveBillingKey(oldMerchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);

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
        String oldMerchantUid = UUID.randomUUID().toString();
        String customerUid = UUID.randomUUID().toString();
        String name = "사과";
        String amount = "100";
        PortOneResponse.OneTimePay oneTimePayResponse = portOneClient.payAndSaveBillingKey(oldMerchantUid, customerUid, name, amount, cardNumber, expiry, birth, password);

        //when
        PortOneResponse.Cancel response = portOneClient.cancel(oneTimePayResponse.getImpUid(), "50");
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }
}