package rhetorike.glot.domain._1auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._1auth.controller.OrgNameController;
import rhetorike.glot.domain._1auth.dto.OrgNameDto;
import rhetorike.glot.setup.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrgNameApiTest extends IntegrationTest {

    @Test
    @DisplayName("[기관명 검색]")
    void searchOrgName() {
        //given
        String KEYWORD = "한국";

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(new OrgNameDto(KEYWORD))
                .contentType(ContentType.JSON)
                .when().post(OrgNameController.SEARCH_URI)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
