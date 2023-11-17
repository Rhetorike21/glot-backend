package rhetorike.glot.domain._1auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import rhetorike.glot.domain._1auth.controller.ResetController;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

public class ResetApiTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("[이메일로 비밀번호 재설정 링크 전송]")
    void sendResetLink() {
        //given
        final String CODE = "123564";
        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        RestAssured.given().log().all()
                .body(new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, CODE, "한국고등학교"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .then().log().all()
                .extract();
        PasswordResetDto.LinkRequest requestDto = new PasswordResetDto.LinkRequest("asdf1234", "김철수", "test@personal.com");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(ResetController.SEND_RESET_LINK_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("[비밀번호 재설정]")
    void resetPassword() {
        //given
        final String CODE = "123564";
        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        RestAssured.given().log().all()
                .body(new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, CODE, "한국고등학교"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .then().log().all()
                .extract();

        final String NEW_PASSWORD = "new-password";
        PasswordResetDto.Request requestDto = new PasswordResetDto.Request("asdf1234", CODE, NEW_PASSWORD);
        given(certCodeRepository.doesExists(CODE)).willReturn(true);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(ResetController.RESET_PASSWORD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(userRepository.findByAccountId("asdf1234").get().getPassword()).isNotEqualTo("abcd1234")
        );

        // 비밀번호 원상복구
        RestAssured.given().log().all()
                .body(new PasswordResetDto.Request("asdf1234", CODE, "abcd1234"))
                .contentType(ContentType.JSON)
                .when().post(ResetController.RESET_PASSWORD_URI)
                .then().log().all()
                .extract();
    }
}
