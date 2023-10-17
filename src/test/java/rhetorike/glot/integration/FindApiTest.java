package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.FindController;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.entity.ResetCode;
import rhetorike.glot.domain._1auth.repository.resetcode.ResetCodeRepository;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.IntegrationTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@Slf4j
public class FindApiTest extends IntegrationTest {

    @MockBean
    ResetCodeRepository resetCodeRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("이메일로 아이디 찾기 (1번 유저)")
    void findAccountIdByEmail() {
        //given
        String name = "테스트용 개인 사용자";
        String email = "test@personal.com";
        AccountIdFindDto.EmailRequest requestDto = new AccountIdFindDto.EmailRequest(name, email);

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
    @DisplayName("이메일로 비밀번호 찾기 (1번 유저)")
    void findPasswordByEmail() {
        //given
        String accountId = "test01personal";
        String name = "테스트용 개인 사용자";
        String email = "test@personal.com";
        PasswordResetDto.EmailRequest requestDto = new PasswordResetDto.EmailRequest(accountId, name, email);

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
    @DisplayName("비밀번호 재설정 (1번 유저)")
    void resetPassword() {
        //given
        String accountId = "test01personal";
        String code = "123456789";
        String oldPassword = userRepository.findByAccountId(accountId).get().getPassword();
        String newPassword = "new-password";
        PasswordResetDto.ResetRequest requestDto = new PasswordResetDto.ResetRequest(accountId, code, newPassword);
        given(resetCodeRepository.findByAccountId(accountId)).willReturn(Optional.of(ResetCode.from(accountId, code)));

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
                () -> assertThat(userRepository.findByAccountId(accountId).get().getPassword()).isNotEqualTo(oldPassword)
        );

        // 비밀번호 원상복구
        RestAssured.given().log().all()
                .body(new PasswordResetDto.ResetRequest(accountId, code, "abcd1234"))
                .contentType(ContentType.JSON)
                .when().post(FindController.RESET_PASSWORD)
                .then().log().all()
                .extract();
    }
}
