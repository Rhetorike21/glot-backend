package rhetorike.glot.setup;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

@ActiveProfiles("inttest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    protected final static String USER_1_ACCOUNT_ID = "test01personal";
    protected final static String USER_1_PASSWORD_RAW = "abcd1234";
    protected final static String USER_1_MOBILE = "01012345678";
    protected final static String USER_1_EMAIL = "test@personal.com";
    protected final static String USER_1_NAME = "테스트용 개인 사용자";

    @LocalServerPort
    int port;

    @BeforeEach
    void init() {
        RestAssured.port = port;
    }

    protected TokenDto.FullResponse getTokenFromUser1() {
        LoginDto requestDto = new LoginDto(USER_1_ACCOUNT_ID, USER_1_PASSWORD_RAW);
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
