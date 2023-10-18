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
import rhetorike.glot.domain._1auth.service.CertificationService;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@Slf4j
public class FindApiTest extends IntegrationTest {

    @MockBean
    CertificationService certificationService;

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
        given(certificationService.isValidNumber(CODE)).willReturn(true);

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
}
