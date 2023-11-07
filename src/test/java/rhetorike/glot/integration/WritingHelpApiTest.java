package rhetorike.glot.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import rhetorike.glot.domain._3writing.controller.WritingBoardController;
import rhetorike.glot.domain._3writing.controller.WritingHelpController;
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._3writing.dto.WritingHelpDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.setup.IntegrationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@Disabled
public class WritingHelpApiTest extends IntegrationTest {

    @Autowired
    WritingBoardRepository writingBoardRepository;

    @Test
    @DisplayName("[AI 작문 추천] - 발전형")
    void helpWithProgress() {
        //given
        final String sentence = "나는 바보 아니다.";
        final String type = "progress";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, type);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingHelpController.WRITING_HELP_API)
                .then().log().all()
                .extract();

        //then
        List<String> result = response.jsonPath().getList("result");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result).isNotEmpty()
        );
    }

    @Test
    @DisplayName("[AI 작문 추천] - 반대형")
    void helpWithReverse() {
        //given
        final String sentence = "나는 바보 아니다.";
        final String type = "reverse";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, type);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingHelpController.WRITING_HELP_API)
                .then().log().all()
                .extract();

        //then
        List<String> result = response.jsonPath().getList("result");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result).isNotEmpty()
        );
    }

    @Test
    @DisplayName("[AI 작문 추천] - 결론형")
    void helpWithConclusion() {
        //given
        final String sentence = "나는 바보 아니다.";
        final String type = "conclusion";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, type);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(ContentType.JSON)
                .when().post(WritingHelpController.WRITING_HELP_API)
                .then().log().all()
                .extract();

        //then
        List<String> result = response.jsonPath().getList("result");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result).isNotEmpty()
        );
    }
}
