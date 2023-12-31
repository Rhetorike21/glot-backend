package rhetorike.glot.domain._2user.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.controller.MockPayIntegrationTest;
import rhetorike.glot.domain._4order.controller.OrderController;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
public class UserApiTest extends MockPayIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("[사용자 정보 조회]")
    void getUserInfo() {
        //given
        String ACCESS_TOKEN = getTokenFromNewUser().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .when().get(UserController.USER_PROFILE_GET_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    @DisplayName("[사용자 프로필 수정] - 개인 계정")
    void updatePersonalProfile() {
        //given
        String ACCESS_TOKEN = getTokenFromNewUser().getAccessToken();
        UserProfileDto.UpdateParam requestBody = new UserProfileDto.UpdateParam("홍길동", "01014828574", null, "sdjflj1234", null);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().patch(UserController.USER_PROFILE_UPDATE_URI)
                .then().log().all()
                .extract();

        List<User> users = userRepository.findByMobileAndName("01014828574", "홍길동");

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(users).hasSize(1),
                () -> assertThat(passwordEncoder.matches("sdjflj1234", users.get(0).getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("[사용자 프로필 수정] - 기관 계정")
    void updateOrganizationProfile() {
        //given
        String ACCESS_TOKEN = getTokenFromNewOrganization().getAccessToken();
        UserProfileDto.UpdateParam requestBody = new UserProfileDto.UpdateParam("김우주", "01023482729", null, "sdjflj1234", null);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().patch(UserController.USER_PROFILE_UPDATE_URI)
                .then().log().all()
                .extract();

        List<User> users = userRepository.findByMobileAndName("01023482729", "김우주");

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(users).hasSize(1),
                () -> assertThat(passwordEncoder.matches("sdjflj1234", users.get(0).getPassword())).isTrue()
        );
    }
}
