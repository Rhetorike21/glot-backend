package rhetorike.glot.domain._1auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.controller.WritingBoardController;
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._4order.controller.MockPayIntegrationTest;
import rhetorike.glot.domain._4order.controller.SubscriptionController;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.util.dto.SingleParamDto;
import rhetorike.glot.setup.IntegrationTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static rhetorike.glot.domain._4order.service.SubscriptionService.SubStatus;

@Slf4j
public class AuthWithdrawApiTest extends MockPayIntegrationTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("[회원탈퇴]")
    void withdraw() {
        //given
        TokenDto.FullResponse token = getTokenFromNewUser();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, token.getAccessToken())
                .header(Header.REFRESH, token.getRefreshToken())
                .when().post(AuthController.WITHDRAW_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    @DisplayName("[작문 저장된 개인 회원의 탈퇴] - 작문이 저장된 회원의 탈퇴")
    void withdrawWithPersonal() {
        //given
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(null, "제목", "내용");
        TokenDto.FullResponse token = getTokenFromNewUser();
        final String accessToken = token.getAccessToken();
        final String refreshToken = token.getRefreshToken();
        BasicPlan basicPlan = planRepository.save(BasicPlan.builder().name("").expiryPeriod(PlanPeriod.MONTH).price(100).discountedPrice(100).build());
        String orderId = makeOrder(accessToken, basicPlan, 1);

        //when
        RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.SAVE_BOARD_URI)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .header(Header.REFRESH, refreshToken)
                .when().post(AuthController.WITHDRAW_URI)
                .then().log().all()
                .extract();

        //then
        Optional<Order> order = orderRepository.findById(orderId);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order).isEmpty()
        );
    }

    @Test
    @DisplayName("[작문 저장된 기관 회원의 탈퇴]")
    void withdrawWithOrganization() {
        //given
        //기관 엔터프라이즈 요금제 구매
        TokenDto.FullResponse orgToken = getTokenFromNewOrganization();
        String orgAccessToken = orgToken.getAccessToken();
        String orgRefreshToken = orgToken.getRefreshToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(orgAccessToken, enterprisePlan, 10);

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
                .header(Header.AUTH, orgAccessToken)
                .when().get(SubscriptionController.GET_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        String[] resultAccountIds = response1.jsonPath().getList("", SubscriptionDto.MemberResponse.class).stream()
                .map(SubscriptionDto.MemberResponse::getAccountId)
                .toArray(String[]::new);

        String accountId1 = resultAccountIds[0];

        // 기관 멤버 로그인
        LoginDto.Request loginRequestDto = new LoginDto.Request(accountId1, accountId1);
        JsonPath jsonPath = RestAssured.given().log().all()
                .body(loginRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract().jsonPath();

        String memberAccessToken = jsonPath.getString("token.accessToken");

        //when
        // 멤버 작문
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(null, "제목", "내용");
        RestAssured.given().log().all()
                .header(Header.AUTH, memberAccessToken)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.SAVE_BOARD_URI)
                .then().log().all()
                .extract();


        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, orgAccessToken)
                .header(Header.REFRESH, orgRefreshToken)
                .when().post(AuthController.WITHDRAW_URI)
                .then().log().all()
                .extract();

        //then
        Optional<Order> order = orderRepository.findById(orderId);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order).isEmpty()
        );
    }


    @Test
    @DisplayName("[작문 저장된 기관 멤버의 회원 탈퇴]")
    void withdrawWithMember(){
        //given
        //기관 엔터프라이즈 요금제 구매
        String accessToken = getTokenFromNewOrganization().getAccessToken();
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, "엔터프라이즈 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));
        String orderId = makeOrder(accessToken, enterprisePlan, 10);

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .when().get(SubscriptionController.GET_SUBS_MEMBER_URI)
                .then().log().all()
                .extract();

        String[] resultAccountIds = response1.jsonPath().getList("", SubscriptionDto.MemberResponse.class).stream()
                .map(SubscriptionDto.MemberResponse::getAccountId)
                .toArray(String[]::new);

        String accountId1 = resultAccountIds[0];

        // 기관 멤버 로그인
        LoginDto.Request loginRequestDto = new LoginDto.Request(accountId1, accountId1);
        JsonPath jsonPath = RestAssured.given().log().all()
                .body(loginRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract().jsonPath();

        String memberAccessToken = jsonPath.getString("token.accessToken");
        String memberRefreshToken = jsonPath.getString("token.refreshToken");

        //when
        // 멤버 작문
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(null, "제목", "내용");
        RestAssured.given().log().all()
                .header(Header.AUTH, memberAccessToken)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.SAVE_BOARD_URI)
                .then().log().all()
                .extract();
        //멤버 회원 탈퇴
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, memberAccessToken)
                .header(Header.REFRESH, memberRefreshToken)
                .when().post(AuthController.WITHDRAW_URI)
                .then().log().all()
                .extract();

        //then
        Order order = orderRepository.findById(orderId).get();
        Subscription subscription = order.getSubscription();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(order).isNotNull(),
                () -> assertThat(subscription).isNotNull(),
                () -> assertThat(subscription.getMembers()).hasSize(9)
        );
    }




}
