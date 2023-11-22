package rhetorike.glot.domain._4order.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.boot.test.mock.mockito.MockBean;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.service.PayService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.IntegrationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class MockPayIntegrationTest extends IntegrationTest {

    @MockBean
    PayService payService;

    protected String makeOrder(String accessToken, Plan plan, int quantity) {
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));
        OrderDto.MakeRequest subsDto = new OrderDto.MakeRequest(plan.getId(), quantity, new Payment("cardNumber", "expiry", "birth", "password"));
        return RestAssured.given().log().all()
                .body(subsDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");
    }
}
