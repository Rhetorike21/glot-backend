package rhetorike.glot.domain._4order.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rhetorike.glot.domain._4order.entity.Order;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class OrderDtoTest {


    @Test
    @DisplayName("결제일을 기준으로 종료일을 설정한다.")
    void test(){
        //given
        LocalDate startDate1 = LocalDate.of(2023, 11, 1);
        LocalDate startDate2 = LocalDate.of(2023, 9, 14);

        //when
        LocalDate endDate1 = startDate1.plusMonths(1).minusDays(1);
        LocalDate endDate2 = startDate2.plusMonths(1).minusDays(1);

        //then
        assertThat(endDate1).isEqualTo(LocalDate.of(2023, 11, 30));
        assertThat(endDate2).isEqualTo(LocalDate.of(2023, 10, 13));
    }


    @Test
    @DisplayName("주어진 카드번호를 4자리마다 구분하고, 마지막 4자리를 제외하고는 마스킹한다.")
    void maskingCardNumber(){
        //given
        String cardNumber = "1234123412341234";

        //when
        String result = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);

        //then
        assertThat(result).isEqualTo("****-****-****-1234");


    }


}