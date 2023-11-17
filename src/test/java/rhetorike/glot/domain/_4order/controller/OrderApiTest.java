package rhetorike.glot.domain._4order.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.PayService;
import rhetorike.glot.domain._4order.service.SubscriptionRenewScheduler;
import rhetorike.glot.domain._4order.service.SubscriptionService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.IntegrationTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ActiveProfiles("secret")
@Disabled
public class OrderApiTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    SubscriptionService subscriptionService;

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
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        }
        OrderDto.BasicOrderRequest requestDto = new OrderDto.BasicOrderRequest(PlanPeriod.MONTH.getName(), new Payment(cardNumber, expiry, birth, password));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_BASIC_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 구매]")
    void orderEnterprisePlan() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "월 엔터프라이즈 요금제", 100L, 100L, PlanPeriod.MONTH));
        }
        OrderDto.EnterpriseOrderRequest requestDto = new OrderDto.EnterpriseOrderRequest(PlanPeriod.MONTH.getName(), 3, new Payment(cardNumber, expiry, birth, password));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ENTERPRISE_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        String orderId = jsonPath.getString("data");
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(subscription.getMembers()).hasSize(3)
        );
    }



    @Test
    @DisplayName("[베이직 요금제 주문 내역 조회]")
    void getHistoryFromBasic() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new BasicPlan(null, "월 베이직 요금제", 100L, 100L, PlanPeriod.MONTH));
        }
        String orderId = orderBasicPlan(accessToken, PlanPeriod.MONTH);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        List<OrderDto.GetResponse> list = response.jsonPath().getList("", OrderDto.GetResponse.class);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(list).hasSize(1),
                () -> assertThat(list.get(0).getStatus()).isEqualTo(OrderStatus.PAID.getDescription()),
                () -> assertThat(subscription.getMembers()).hasSize(1)
        );
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 주문 내역 조회]")
    void getHistoryFromEnterPrise() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "월 엔터프라이즈 요금제", 100L, 100L, PlanPeriod.MONTH));
        }
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 3);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        List<OrderDto.GetResponse> list = response.jsonPath().getList("", OrderDto.GetResponse.class);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(list).hasSize(1),
                () -> assertThat(list.get(0).getStatus()).isEqualTo(OrderStatus.PAID.getDescription()),
                () -> assertThat(subscription.getMembers()).hasSize(3)
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


    @Test
    @DisplayName("[베이직 요금제 자동 재결제]")
    void basicRepay() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        }
        String orderId = orderBasicPlan(accessToken, PlanPeriod.MONTH);
        log.info(orderId);

        //when
        orderService.reorder(LocalDate.now().plusMonths(1).minusDays(1));
        subscriptionService.deleteOverdue(LocalDate.now().plusMonths(1).minusDays(1));

        //then
        Order orderBefore = orderRepository.findById(orderId).get();
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(orderBefore.getUser());
        assertThat(orderBefore.getSubscription()).isNull();
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 자동 재결제]")
    void enterpriseRepay() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L,100L,  PlanPeriod.MONTH));
        }
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 3);
        log.info(orderId);

        //when
        orderService.reorder(LocalDate.now().plusMonths(1).minusDays(1));
        subscriptionService.deleteOverdue(LocalDate.now().plusMonths(1).minusDays(1));

        //then
        Order orderBefore = orderRepository.findById(orderId).get();
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(orderBefore.getUser());
        assertThat(orderBefore.getSubscription()).isNull();
        assertThat(orders).hasSize(2);
    }


    @Test
    @DisplayName("[환불] - 베이직 월간 요금제, 결제일 7일 경과 전")
    void refundBasicMonthly(){
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        }
        String orderId = orderBasicPlan(accessToken, PlanPeriod.MONTH);
        log.info(orderId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().post(OrderController.REFUND_URI)
                .then().log().all()
                .extract();

        //then
        Order order = orderRepository.findById(orderId).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED),
                () -> assertThat(order.getSubscription()).isNull()
        );
    }

    @Test
    @DisplayName("[환불] - 베이직 연간 요금제, 결제일 7일 경과 전")
    void refundBasicYearly(){
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.YEAR).isEmpty()) {
            planRepository.save(new BasicPlan(null, "베이직 요금제 연간 결제", 100L, 100L, PlanPeriod.YEAR));
        }
        String orderId = orderBasicPlan(accessToken, PlanPeriod.YEAR);
        log.info(orderId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().post(OrderController.REFUND_URI)
                .then().log().all()
                .extract();

        //then
        Order order = orderRepository.findById(orderId).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED),
                () -> assertThat(order.getSubscription()).isNull()
        );
    }

    @Test
    @DisplayName("[환불] - 엔터프라이즈 월간 요금제, 결제일 7일 경과 전")
    void refundEnterpriseMonthly(){
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L,100L,  PlanPeriod.MONTH));
        }
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 3);
        log.info(orderId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().post(OrderController.REFUND_URI)
                .then().log().all()
                .extract();

        //then
        Order order = orderRepository.findById(orderId).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED),
                () -> assertThat(order.getSubscription()).isNull()
        );
    }

    @Test
    @DisplayName("[환불] - 엔터프라이즈 연간 요금제, 결제일 7일 경과 전")
    void refundEnterpriseYearly(){
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.YEAR).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 연간 결제", 100L,100L,  PlanPeriod.YEAR));
        }
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.YEAR, 3);
        log.info(orderId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().post(OrderController.REFUND_URI)
                .then().log().all()
                .extract();

        //then
        Order order = orderRepository.findById(orderId).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED),
                () -> assertThat(order.getSubscription()).isNull()
        );
    }

    @Test
    @DisplayName("[환불 정보 조회]")
    void getRefundInfo(){
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L,100L,  PlanPeriod.MONTH));
        }
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 3);
        log.info(orderId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().get(OrderController.REFUND_INFO_URI)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getString("accountId")).isNotNull(),
                () -> assertThat(jsonPath.getInt("numOfMembers")).isEqualTo(3),
                () -> assertThat(jsonPath.getInt("remainDays")).isEqualTo(29),
                () -> assertThat(jsonPath.getInt("refundAmount")).isNotNull()
        );
    }




    private String orderBasicPlan(String accessToken, PlanPeriod planPeriod) {
        OrderDto.BasicOrderRequest requestDto = new OrderDto.BasicOrderRequest(planPeriod.getName(), new Payment(cardNumber, expiry, birth, password));
        return RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_BASIC_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");

    }

    private String orderEnterprisePlan(String accessToken, PlanPeriod planPeriod, int quantity) {
        OrderDto.EnterpriseOrderRequest requestDto = new OrderDto.EnterpriseOrderRequest(planPeriod.getName(), quantity, new Payment(cardNumber, expiry, birth, password));
        return RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ENTERPRISE_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");
    }
}
