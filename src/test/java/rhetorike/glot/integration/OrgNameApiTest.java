package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.CertificationController;
import rhetorike.glot.domain._1auth.controller.OrgNameController;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrgNameApiTest extends IntegrationTest {

    @Test
    @DisplayName("기관을 검색한다.")
    void searchOrgName(){
        //given
        String keyword = "영어";

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword",keyword)
                .when().get(OrgNameController.SEARCH_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
