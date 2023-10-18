package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.CertificationController;
import rhetorike.glot.domain._1auth.controller.FindController;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.dto.CertificationDto;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.entity.ResetCode;
import rhetorike.glot.domain._1auth.repository.resetcode.ResetCodeRepository;
import rhetorike.glot.domain._1auth.service.smscert.SmsCertificationService;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.IntegrationTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@Slf4j
public class FindApiTest extends IntegrationTest {

    @MockBean
    ResetCodeRepository resetCodeRepository;
    @MockBean
    SmsCertificationService smsCertificationService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("[이메일로 아이디 찾기]")
    void findAccountIdByEmail() {
        //given
        AccountIdFindDto.EmailRequest requestDto = new AccountIdFindDto.EmailRequest(USER_1_NAME, USER_1_EMAIL);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(FindController.FIND_ACCOUNT_ID_BY_EMAIL)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("[휴대폰으로 아이디 찾기]")
    void findAccountIdByMobile() {
        //given
        final String CODE = "123456";
        CertificationDto.CodeRequest requestBody = new CertificationDto.CodeRequest(USER_1_MOBILE);
        AccountIdFindDto.MobileRequest requestDto = new AccountIdFindDto.MobileRequest(USER_1_NAME, USER_1_MOBILE, CODE);
        given(smsCertificationService.verifyCode(CODE)).willReturn(true);
        given(smsCertificationService.doesValidPinNumbers(CODE)).willReturn(true);

        /* 인증코드 전송 */
        RestAssured
                .given().log().all()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().post(CertificationController.SEND_CODE_URI)
                .then().log().all()
                .extract();
        /* 인증코드 검증 */
        RestAssured
                .given().log().all()
                .queryParam("code", CODE)
                .when().post(CertificationController.VERIFY_CODE_URI)
                .then().log().all()
                .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(FindController.FIND_ACCOUNT_ID_BY_MOBILE)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("accountIds")).containsExactly(USER_1_ACCOUNT_ID)
        );
    }

    @Test
    @DisplayName("[이메일로 비밀번호 찾기]")
    void findPasswordByEmail() {
        //given
        PasswordResetDto.EmailRequest requestDto = new PasswordResetDto.EmailRequest(USER_1_ACCOUNT_ID, USER_1_NAME, USER_1_EMAIL);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(FindController.FIND_PASSWORD_BY_EMAIL)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("[비밀번호 재설정]")
    void resetPassword() {
        //given
        final String CODE = UUID.randomUUID().toString();
        final String NEW_PASSWORD = "new-password";
        PasswordResetDto.ResetRequest requestDto = new PasswordResetDto.ResetRequest(USER_1_ACCOUNT_ID, CODE, NEW_PASSWORD);
        given(resetCodeRepository.findByAccountId(USER_1_ACCOUNT_ID)).willReturn(Optional.of(ResetCode.from(USER_1_ACCOUNT_ID, CODE)));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(FindController.RESET_PASSWORD)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(userRepository.findByAccountId(USER_1_ACCOUNT_ID).get().getPassword()).isNotEqualTo(USER_1_PASSWORD_RAW)
        );

        // 비밀번호 원상복구
        RestAssured.given().log().all()
                .body(new PasswordResetDto.ResetRequest(USER_1_ACCOUNT_ID, CODE, USER_1_PASSWORD_RAW))
                .contentType(ContentType.JSON)
                .when().post(FindController.RESET_PASSWORD)
                .then().log().all()
                .extract();
    }
}
