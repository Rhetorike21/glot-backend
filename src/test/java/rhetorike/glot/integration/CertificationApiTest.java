package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.CertificationController;
import rhetorike.glot.domain._1auth.dto.CertificationDto;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CertificationApiTest extends IntegrationTest {

    @Test
    @DisplayName("주어진 전화번호로 인증번호를 전송한다.")
    void sendCodeBySms(){
        //given
        CertificationDto.CodeRequest requestBody = new CertificationDto.CodeRequest("01023456789");

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
    @DisplayName("인증번호를 확인한다.")
    void verifyCode(){
        //given

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("code","1234")
                .when().post(CertificationController.VERIFY_CODE_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
