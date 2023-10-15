package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.SignUpRequest;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.CertCodeRepository;
import rhetorike.glot.setup.IntegrationTest;

import java.util.Optional;

public class AuthApiTest extends IntegrationTest {

    @MockBean
    CertCodeRepository certCodeRepository;

    @Test
    @DisplayName("개인 사용자로 회원가입한다.")
    void signUpWithPersonal() {
        //given
        String pinNumbers = "1234";
        given(certCodeRepository.findByPinNumbers(pinNumbers)).willReturn(Optional.of(new CertCode(pinNumbers, true)));
        SignUpRequest.PersonalDto requestDto = new SignUpRequest.PersonalDto("testpersonal", "abc1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, pinNumbers);

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
    @DisplayName("기관 사용자로 회원가입한다.")
    void signUpWithOrganization() {
        //given
        String pinNumbers = "1234";
        given(certCodeRepository.findByPinNumbers(pinNumbers)).willReturn(Optional.of(new CertCode(pinNumbers, true)));
        SignUpRequest.OrganizationDto requestDto = new SignUpRequest.OrganizationDto("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, pinNumbers, "한국고등학교");

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

}
