package rhetorike.glot.setup;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

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

//    @MockBean
//    protected KakaoLoginStrategy kakaoLoginStrategy;
//
////    protected String getAccessToken(){
////        String socialToken = "social-token";
////        PersonalData personalData = new PersonalData("김철수", "chul@naver.com", "xyzabc", LoginType.KAKAO);
////        given(kakaoLoginStrategy.getPersonalData(socialToken)).willReturn(personalData);
////
////        // when
////        ExtractableResponse<Response> response = RestAssured
////                .given().log().all()
////                .header(Header.SOCIAL, socialToken)
////                .when().post(LOGIN_API, LoginType.KAKAO.getName())
////                .then().log().all()
////                .extract();
////
////        // then
////        JsonPath jsonPath = response.jsonPath();
////        return jsonPath.getString("accessToken");
////    }
////
////    protected ServiceToken getTokenSet(){
////        String socialToken = "social-token";
////        PersonalData personalData = new PersonalData("김철수", "chul@naver.com", "xyzabc", LoginType.KAKAO);
////        given(kakaoLoginStrategy.getPersonalData(socialToken)).willReturn(personalData);
////
////        // when
////        ExtractableResponse<Response> response = RestAssured
////                .given().log().all()
////                .header(Header.SOCIAL, socialToken)
////                .when().post(LOGIN_API, LoginType.KAKAO.getName())
////                .then().log().all()
////                .extract();
////
////        // then
////        JsonPath jsonPath = response.jsonPath();
////        return jsonPath.getObject("$", ServiceToken.class);
////    }
}
