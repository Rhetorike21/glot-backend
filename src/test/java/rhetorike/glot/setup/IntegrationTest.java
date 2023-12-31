package rhetorike.glot.setup;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._1auth.controller.AuthController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

import static org.mockito.BDDMockito.given;

@ActiveProfiles("inttest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    int port;

    @MockBean
    protected CertCodeRepository certCodeRepository;

    @BeforeEach
    void init() {
        RestAssured.port = port;
    }


    protected TokenDto.FullResponse getTokenFromNewUser() {
        final String CODE = "1234";
        String str = RandomStringUtils.randomAlphabetic(4);
        String num = RandomStringUtils.randomNumeric(4);
        final String name = "test";
        final String accountId = str + num;
        final String password = str + num;
        final String mobile = "010" + "1234" + num;
        final String email = str + num + "@personal.com";

        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        SignUpDto.PersonalRequest signupRequestDto = new SignUpDto.PersonalRequest(accountId, password, name, mobile, mobile, email, true, CODE);
        RestAssured.given().log().all()
                .body(signupRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_PERSONAL_URI)
                .then().log().all()
                .extract();

        LoginDto.Request loginRequestDto = new LoginDto.Request(accountId, password);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(loginRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract();

        JsonPath jsonPath = response.jsonPath();
        AccessToken accessToken = AccessToken.from(jsonPath.getString("token.accessToken"));
        RefreshToken refreshToken = RefreshToken.from(jsonPath.getString("token.refreshToken"));
        return new TokenDto.FullResponse(accessToken, refreshToken);
    }

    protected TokenDto.FullResponse getTokenFromNewOrganization() {
        final String CODE = "1234";
        String str = RandomStringUtils.randomAlphabetic(4);
        String num = RandomStringUtils.randomNumeric(4);
        final String accountId = str + num;
        final String password = str + num;
        final String mobile = "010" + "1234" + num;
        final String email = str + num + "@personal.com";
        final String orgName = "test_org_" + RandomStringUtils.randomAlphabetic(4);


        given(certCodeRepository.doesExists(CODE)).willReturn(true);
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest(accountId, password, "김철수", mobile, mobile, email, true, CODE, orgName);
        RestAssured.given().log().all()
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .then().log().all()
                .extract();

        LoginDto.Request loginRequestDto = new LoginDto.Request(accountId, password);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(loginRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(AuthController.LOGIN_URI)
                .then().log().all()
                .extract();


        JsonPath jsonPath = response.jsonPath();
        AccessToken accessToken = AccessToken.from(jsonPath.getString("token.accessToken"));
        RefreshToken refreshToken = RefreshToken.from(jsonPath.getString("token.refreshToken"));
        return new TokenDto.FullResponse(accessToken, refreshToken);
    }
}


