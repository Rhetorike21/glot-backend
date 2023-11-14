package rhetorike.glot.domain._4order.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.PayService;
import rhetorike.glot.domain._4order.service.SubscriptionService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.IntegrationTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
public class SubscriptionApiTest extends IntegrationTest {

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

    @MockBean
    PayService payService;

    @Test
    @DisplayName("[베이직 요금제 구독 취소]")
    void stopBasicSubscription() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        if (planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, PlanPeriod.MONTH));
        }
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));
        String orderId = orderBasicPlan(accessToken, PlanPeriod.MONTH);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().delete(SubscriptionController.UNSUBSCRIBE_URI)
                .then().log().all()
                .extract();

        //then
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(subscription.isContinued()).isFalse()
        );
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 구독 취소]")
    void stopEnterpriseSubscription() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, PlanPeriod.MONTH));
        }

        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 3);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().delete(SubscriptionController.UNSUBSCRIBE_URI)
                .then().log().all()
                .extract();

        //then
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(subscription.isContinued()).isFalse()
        );
    }

    @Test
    @DisplayName("[구독 계정 조회]")
    void getSubscriptionMembers() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, PlanPeriod.MONTH));
        }

        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 10);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().get(SubscriptionController.GET_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        //then
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        List<String> foundAccountIds = subscription.getMembers().stream()
                .map(User::getAccountId)
                .toList();
        String[] resultAccountIds = response.jsonPath().getList("", SubscriptionDto.MemberResponse.class).stream()
                .map(SubscriptionDto.MemberResponse::getAccountId)
                .toArray(String[]::new);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(foundAccountIds).containsExactly(resultAccountIds)
        );
    }

    @Test
    @DisplayName("[구독 계정 조회] - 마지막 접속 시간 확인")
    void getSubscriptionMembersLastLoggedInAt() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        if (planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH).isEmpty()) {
            planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, PlanPeriod.MONTH));
        }
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));
        String orderId = orderEnterprisePlan(accessToken, PlanPeriod.MONTH, 10);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();

        String memberAccountId1 = subscription.getMembers().get(0).getAccountId();

        //when
        LoginDto requestDto = new LoginDto(memberAccountId1, memberAccountId1);
        RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().get(SubscriptionController.GET_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        //then
        List<SubscriptionDto.MemberResponse> list = response.jsonPath().getList("", SubscriptionDto.MemberResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(list.get(0).getLastLog()).isNotNull()
        );

    }


    private String orderBasicPlan(String accessToken, PlanPeriod planPeriod) {
        OrderDto.BasicOrderRequest requestDto = new OrderDto.BasicOrderRequest(planPeriod.getName(), new Payment("cardNumber", "expiry", "birth", "password"));
        return RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_BASIC_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");

    }

    private String orderEnterprisePlan(String accessToken, PlanPeriod planPeriod, int quantity) {
        OrderDto.EnterpriseOrderRequest requestDto = new OrderDto.EnterpriseOrderRequest(planPeriod.getName(), quantity, new Payment("cardNumber", "expiry", "birth", "password"));
        return RestAssured.given().log().all()
                .body(requestDto)
                .header(Header.AUTH, accessToken)
                .contentType(ContentType.JSON)
                .when().post(OrderController.MAKE_ENTERPRISE_ORDER_URI)
                .then().log().all()
                .extract().jsonPath().get("data");
    }
}
