package rhetorike.glot.domain._1auth.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

@Slf4j
public class AuthApiTest extends IntegrationTest {

    @Test
    @DisplayName("[개인 사용자 회원가입]")
    void signUpWithPersonal() {
        //given
        final String CODE = "1234";
        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest("test1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, CODE);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_PERSONAL_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    @DisplayName("[기관 사용자 회원가입]")
    void signUpWithOrganization() {
        //given
        final String CODE = "123564";
        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, CODE, "한국고등학교");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    @DisplayName("[로그인]")
    void login() {
        //given
        LoginDto.Request loginRequestDto = new LoginDto.Request("asdf1234", "abcd1234");
        final String CODE = "123564";
        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, CODE, "한국고등학교");
        RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .then().log().all()
                .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(loginRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getBoolean("subscribed")).isFalse(),
                () -> assertThat(jsonPath.getString("token.accessToken")).isNotEmpty(),
                () -> assertThat(jsonPath.getString("token.refreshToken")).isNotEmpty()
        );
    }

    @Test
    @DisplayName("[로그아웃]")
    void logout() {
        //given
        TokenDto.FullResponse tokenResponse = getTokenFromNewUser();
        final String ACCESS_TOKEN = tokenResponse.getAccessToken();
        final String REFRESH_TOKEN = tokenResponse.getRefreshToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .header(Header.REFRESH, REFRESH_TOKEN)
                .when().post(AuthController.LOGOUT_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

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
    @DisplayName("[액세스 토큰 재발급]")
    void reissue() {
        //given
        TokenDto.FullResponse tokenResponse = getTokenFromNewUser();
        final String ACCESS_TOKEN = tokenResponse.getAccessToken();
        final String REFRESH_TOKEN = tokenResponse.getRefreshToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .header(Header.REFRESH, REFRESH_TOKEN)
                .when().post(AuthController.REISSUE_URI)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getString("accessToken")).isNotEmpty()
        );
    }
}
