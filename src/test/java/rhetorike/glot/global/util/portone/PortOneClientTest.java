package rhetorike.glot.global.util.portone;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.setup.IntegrationTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PortOneClientTest extends IntegrationTest {

    @Autowired
    PortOneClient portOneClient;

    @Test
    @DisplayName("[액세스 토큰 발급]")
    void getAccessToken() {
        //given

        //when
        String result = portOneClient.getAccessToken().getAccessToken();
        log.info(result);

        //then
        assertThat(result).isNotEmpty();
    }


    @Test
    @DisplayName("[최초 결제 및 키 발급]")
    void payAndSaveBillingKey() {
        //given
        String merchantUid = UUID.randomUUID().toString();

        //when
        PortOneClient.OneTimePayResponse response = portOneClient.payAndSaveBillingKey(merchantUid);
        log.info("{}", response);


        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }


    @Test
    @DisplayName("[결제 내역 조회]")
    void getPaymentsHistory() {
        //given
        String merchantUid = UUID.randomUUID().toString();
        String impUid = portOneClient.payAndSaveBillingKey(merchantUid).getImpUid();

        //when
        PortOneClient.HistoryResponse response = portOneClient.getPaymentsHistory(impUid);
        log.info("{}", response);


        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }

    @Test
    @Disabled
    @DisplayName("[재결제]")
    void payAgain() {
        //given
        String merchantUid = UUID.randomUUID().toString();
        String customerUid = "abcd"; // 추후 빌링키 등록 시, customerUid 명시해야 함

        //when
        PortOneClient.AgainResponse response = portOneClient.payAgain(customerUid, merchantUid);
        log.info("{}", response);

        //then
        assertThat(response.getImpUid()).isNotEmpty();
    }
}