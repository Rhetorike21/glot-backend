package rhetorike.glot.setup;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

import static org.mockito.BDDMockito.given;

@ActiveProfiles("inttest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void init() {
        RestAssured.port = port;
    }

    protected TokenDto.FullResponse getToken() {
        //given
        String id = "test01personal";
        String password = "abcd1234";
        LoginDto requestDto = new LoginDto(id, password);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract();

        JsonPath jsonPath = response.jsonPath();
        AccessToken accessToken = AccessToken.from(jsonPath.getString("accessToken"));
        RefreshToken refreshToken = RefreshToken.from(jsonPath.getString("refreshToken"));
        return new TokenDto.FullResponse(accessToken, refreshToken);
    }

}
