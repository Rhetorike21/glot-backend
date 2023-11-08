package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._1auth.controller.ResetController;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.controller.OrderController;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("secret")
//@Disabled
public class OrderApiTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanRepository planRepository;

    @Value("${pay.card}")
    private String cardNumber;
    @Value("${pay.expiry}")
    private String expiry;
    @Value("${pay.birth}")
    private String birth;
    @Value("${pay.password}")
    private String password;

    @Test
    @DisplayName("[베이직 요금제 구매]")
    void orderBasicPlan() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        Long planId = planRepository.save(new BasicPlan(null, "월 베이직 요금제", 100L, Period.ofMonths(1))).getId();
        OrderDto.MakeRequest requestDto = new OrderDto.MakeRequest(planId, 1, new Payment(cardNumber, expiry, birth, password));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("[주문 내역 조회]")
    void getHistory() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        Long planId = planRepository.save(new BasicPlan(null, "월 베이직 요금제", 100L, Period.ofMonths(1))).getId();
        orderPlan(accessToken, planId, 1);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        List<OrderDto.GetResponse> list = response.jsonPath().getList("");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(list).hasSize(1)
        );
    }

    @Test
    @DisplayName("[지불 방식 변경]")
    void updatePayMethod() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        Payment requestBody = new Payment(cardNumber, expiry, birth, password);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().patch(OrderController.CHANGE_PAY_METHOD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    private void orderPlan(String accessToken, Long planId, int quantity){
        OrderDto.MakeRequest requestDto = new OrderDto.MakeRequest(planId, quantity, new Payment(cardNumber, expiry, birth, password));
        RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
                .then().log().all()
                .extract();
    }
}
