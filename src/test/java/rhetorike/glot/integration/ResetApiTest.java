package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.ResetController;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
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
        PasswordResetDto.LinkRequest requestDto = new PasswordResetDto.LinkRequest(USER_1_ACCOUNT_ID, USER_1_NAME, USER_1_EMAIL);

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
        final String CODE = "123456";
        final String NEW_PASSWORD = "new-password";
        PasswordResetDto.Request requestDto = new PasswordResetDto.Request(USER_1_ACCOUNT_ID, CODE, NEW_PASSWORD);
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
                () -> assertThat(userRepository.findByAccountId(USER_1_ACCOUNT_ID).get().getPassword()).isNotEqualTo(USER_1_PASSWORD_RAW)
        );

        // 비밀번호 원상복구
        RestAssured.given().log().all()
                .body(new PasswordResetDto.Request(USER_1_ACCOUNT_ID, CODE, USER_1_PASSWORD_RAW))
                .contentType(ContentType.JSON)
                .when().post(ResetController.RESET_PASSWORD_URI)
                .then().log().all()
                .extract();
    }
}
