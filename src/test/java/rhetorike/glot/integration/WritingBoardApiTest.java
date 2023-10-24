package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._3writing.controller.WritingBoardController;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertAll;

public class WritingBoardApiTest extends IntegrationTest {

    @Test
    @DisplayName("[작문 보드 생성] - 비회원")
    void createBoardByUnknown() {
        //given
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        final String ACCESS_TOKEN = getTokenFromUser1().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.CREATE_WRITING_BOARD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    @Test
    @DisplayName("[작문 보드 생성] - 회원")
    void createBoardByUser() {
        //given
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        final String ACCESS_TOKEN = getTokenFromUser1().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.CREATE_WRITING_BOARD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }
}
