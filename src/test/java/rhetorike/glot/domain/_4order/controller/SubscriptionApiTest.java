package rhetorike.glot.domain._4order.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._2user.controller.UserController;
import rhetorike.glot.domain._2user.dto.UserDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
public class SubscriptionApiTest extends MockPayIntegrationTest {

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

    @Test
    @DisplayName("[베이직 요금제 구독 취소]")
    void stopBasicSubscription() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, basicPlan, 1);

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
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);

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
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 10);

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
    @DisplayName("[구독 계정 정보 수정]")
    void updateSubscriptionMembers() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 10);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        String memberAccountId = subscription.getMembers().get(0).getAccountId();
        log.info(memberAccountId);
        SubscriptionDto.MemberUpdateRequest requestDto = new SubscriptionDto.MemberUpdateRequest(memberAccountId, "zxcv123", "name", null);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().patch(SubscriptionController.UPDATE_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        //then
        User member = userRepository.findByAccountId(memberAccountId).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(member.getName()).isEqualTo("name"),
                () -> assertThat(member.isActive()).isTrue()
        );
    }

    @Test
    @DisplayName("[구독 계정 조회] - 기관 계정만 조회 가능")
    void getSubscriptionMembersAccessDenied() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().get(SubscriptionController.GET_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
        );
    }

    @Test
    @DisplayName("[구독 계정 조회] - 마지막 접속 시간 확인")
    void getSubscriptionMembersLastLoggedInAt() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 10);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        String memberAccountId1 = subscription.getMembers().get(0).getAccountId();

        //when
        LoginDto.Request requestDto = new LoginDto.Request(memberAccountId1, memberAccountId1);
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

    @Test
    @DisplayName("[계정 비활성화/활성화] - 기관 계정")
    void activateUser() {
        //given
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 3);
        Subscription subscription = orderRepository.findById(orderId).get().getSubscription();
        String memberAccountId1 = subscription.getMembers().get(0).getAccountId();
        UserDto.ActivateRequest requestBody = new UserDto.ActivateRequest(memberAccountId1, false);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().post(UserController.ACTIVATE_MEMBER)
                .then().log().all()
                .extract();

        //then
        User member = orderRepository.findById(orderId).get().getSubscription().getOrder().getSubscription().getMembers().get(0);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(member.getAccountId()).isEqualTo(memberAccountId1),
                () -> assertThat(member.isActive()).isFalse()
        );
    }

    @Test
    @DisplayName("[계정 비활성화/활성화] - 기관 계정만 호출 가능")
    void activateUserForbidden() {
        //given
        String accessToken = getTokenFromNewUser().getAccessToken();
        UserDto.ActivateRequest requestBody = new UserDto.ActivateRequest("", false);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().post(UserController.ACTIVATE_MEMBER)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
        );
    }
}
