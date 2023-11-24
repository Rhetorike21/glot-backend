package rhetorike.glot.domain._1auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.CertificationController;
import rhetorike.glot.domain._1auth.dto.CertificationDto;
import rhetorike.glot.domain._1auth.service.codesender.smssender.MobileCodeSender;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class CertificationApiTest extends IntegrationTest {

    @MockBean
    MobileCodeSender mobileCodeSender;

    @Test
    @DisplayName("[인증번호 전송]")
    void sendCodeBySms(){
        //given
        CertificationDto.CodeRequest requestBody = new CertificationDto.CodeRequest("01012345678");

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when().post(CertificationController.SEND_CODE_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("[인증번호 확인]")
    void verifyCode(){
        //given
        final String CODE = "123456";

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("code",CODE)
                .when().post(CertificationController.VERIFY_CODE_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
