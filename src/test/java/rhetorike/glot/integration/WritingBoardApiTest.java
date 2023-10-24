package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class WritingBoardApiTest extends IntegrationTest {

    @Test
    @DisplayName("[작문 보드 생성] - 비회원")
    void createBoardByUnknown() {
        //given
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.CREATE_WRITING_BOARD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    @Test
    @DisplayName("[작문 보드 생성] - 회원")
    void createBoardByUser() {
        //given
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        final String ACCESS_TOKEN = getTokenFromNewUser().getAccessToken();

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
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    @DisplayName("[작문 보드 전체 조회] - 회원")
    void getAllBoards() {
        //given
        final String ACCESS_TOKEN = getTokenFromNewUser().getAccessToken();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .when().get(WritingBoardController.GET_WRITING_BOARD_URI)
                .then().log().all()
                .extract();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    @DisplayName("[작문 보드 전체 조회] - 회원, 3개 생성 후 조회")
    void getAllBoardsThree() {
        //given
        final String ACCESS_TOKEN = getTokenFromNewUser().getAccessToken();
        create(ACCESS_TOKEN, 1);
        create(ACCESS_TOKEN, 2);
        create(ACCESS_TOKEN, 3);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header(Header.AUTH, ACCESS_TOKEN)
                .when().get(WritingBoardController.GET_WRITING_BOARD_URI)
                .then().log().all()
                .extract();

        //then
        JsonPath jsonPath = response.jsonPath();
        List<WritingDto.Response> list = jsonPath.getList("", WritingDto.Response.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(list).hasSize(3),
                () -> assertThat(list.stream().map(WritingDto.Response::getTitle)).containsExactly("제목3", "제목2", "제목1")
        );

    }

    private void create(String accessToken, int order) {
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목" + order);
        RestAssured.given().log().all()
                .header(Header.AUTH, accessToken)
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingBoardController.CREATE_WRITING_BOARD_URI)
                .then().log().all()
                .extract();
    }
}
