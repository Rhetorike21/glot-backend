package rhetorike.glot.domain._4order.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.SubscriptionService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ActiveProfiles("secret")
//@Disabled
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
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        OrderDto.MakeRequest requestDto = new OrderDto.MakeRequest(basicPlan.getId(), 1, new Payment(cardNumber, expiry, birth, password));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 구매]")
    void makeOrder() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        Plan plan = planRepository.save(new EnterprisePlan(null, "월 엔터프라이즈 요금제", 100L, 100L, PlanPeriod.MONTH));
        OrderDto.MakeRequest requestDto = new OrderDto.MakeRequest(plan.getId(), 3, new Payment(cardNumber, expiry, birth, password));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
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
    @DisplayName("[베이직 요금제 주문 내역 조회] - 구독 중인 경우")
    void getHistoryFromBasic() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        Plan plan = planRepository.save(new BasicPlan(null, "월 베이직 요금제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, plan, 1);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        OrderDto.GetResponse getResponse = response.jsonPath().getObject("", OrderDto.GetResponse.class);
        List<OrderDto.History> history = getResponse.getHistory();
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(history).hasSize(1),
                () -> assertThat(history.get(0).getStatus()).isEqualTo(OrderStatus.PAID.getDescription()),
                () -> assertThat(subscription.getMembers()).hasSize(1)
        );
    }

    @Test
    @DisplayName("[베이직 요금제 주문 내역 조회] - 구독 안하는 경우")
    void getHistoryFromFree() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        OrderDto.GetResponse getResponse = response.jsonPath().getObject("", OrderDto.GetResponse.class);
        List<OrderDto.History> history = getResponse.getHistory();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(history).isNull()
        );
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 주문 내역 조회]")
    void getHistoryFromEnterPrise() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "월 엔터프라이즈 요금제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().get(OrderController.GET_ORDER_URI)
                .then().log().all()
                .extract();

        //then
        OrderDto.GetResponse getResponse = response.jsonPath().getObject("", OrderDto.GetResponse.class);
        List<OrderDto.History> history = getResponse.getHistory();
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(history).hasSize(1),
                () -> assertThat(history.get(0).getStatus()).isEqualTo(OrderStatus.PAID.getDescription()),
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
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, basicPlan, 1);

        //when
        orderService.reorder(LocalDate.now().plusMonths(1).minusDays(1));
        subscriptionService.pauseOverdueSubscriptions(LocalDate.now().plusMonths(1).minusDays(1));

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
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);

        //when
        orderService.reorder(LocalDate.now().plusMonths(1).minusDays(1));
        subscriptionService.pauseOverdueSubscriptions(LocalDate.now().plusMonths(1).minusDays(1));

        //then
        Order orderBefore = orderRepository.findById(orderId).get();
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(orderBefore.getUser());
        assertThat(orderBefore.getSubscription()).isNull();
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 자동 재결제] - 비활성 계정 제외")
    void enterpriseRepayWithoutInactive() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 10);
        Order firstOrder = orderRepository.findById(orderId).get();
        Subscription subscription = firstOrder.getSubscription();
        List<User> members = subscription.getMembers();

        RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(new SubscriptionDto.MemberUpdateRequest(members.get(0).getAccountId(), null, null, false))
                .contentType(ContentType.JSON)
                .when().patch(SubscriptionController.UPDATE_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(new SubscriptionDto.MemberUpdateRequest(members.get(5).getAccountId(), null, null, false))
                .contentType(ContentType.JSON)
                .when().patch(SubscriptionController.UPDATE_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        //when
        orderService.reorder(LocalDate.now().plusMonths(1).minusDays(1));
        subscriptionService.pauseOverdueSubscriptions(LocalDate.now().plusMonths(1).minusDays(1));

        //then
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(firstOrder.getUser());
        log.info("{}", orders);
        Order secondOrder = orders.get(0);
        assertAll(
                () -> assertThat(secondOrder.getSupplyPrice()).isEqualTo(800L)
        );
    }


    @Test
    @DisplayName("[환불] - 베이직 월간 요금제, 결제일 7일 경과 전")
    void refundBasicMonthly() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, basicPlan, 1);
        User user = orderRepository.findById(orderId).get().getSubscription().getMembers().get(0);

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
                () -> assertThat(order.getSubscription()).isNull(),
                () -> assertThat(userRepository.findById(user.getId())).isPresent()

        );
    }

    @Test
    @DisplayName("[환불] - 베이직 연간 요금제, 결제일 7일 경과 전")
    void refundBasicYearly() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, basicPlan, 1);
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
    void refundEnterpriseMonthly() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);
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
    void refundEnterpriseYearly() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.YEAR));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);
        log.info(orderId);
        User user = orderRepository.findById(orderId).get().getSubscription().getMembers().get(0);

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
                () -> assertThat(order.getSubscription()).isNull(),
                () -> assertThat(userRepository.findById(user.getId())).isEmpty()
        );
    }

    @Test
    @DisplayName("[환불 정보 조회]")
    void getRefundInfo() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);

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


    private String makeOrder(String accessToken, Plan plan, int quantity) {
        OrderDto.MakeRequest requestDto = new OrderDto.MakeRequest(plan.getId(), quantity, new Payment(cardNumber, expiry, birth, password));
        return RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");
    }
}
