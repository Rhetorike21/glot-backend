package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.FindController;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.setup.IntegrationTest;

public class FindApiTest extends IntegrationTest {


    @Test
    @DisplayName("이메일로 아이디 찾기 (1번 유저)")
    void findAccountIdByEmail(){
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
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
