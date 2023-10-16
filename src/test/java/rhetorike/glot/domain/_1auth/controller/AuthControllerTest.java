package rhetorike.glot.domain._1auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.service.AuthService;
import rhetorike.glot.domain._1auth.service.ReissueService;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.HeaderElement.header;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = AuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;
    @MockBean
    ReissueService reissueService;


    @Test
    @WithMockUser
    @DisplayName("개인 사용자 회원 가입")
    void signUpWithPersonal() throws Exception {
        //given
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest("testpersonal", "abc1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234");

        //when
        ResultActions actions = mockMvc.perform(post(AuthController.SIGN_UP_PERSONAL_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("sign-up-with-personal",
                        requestFields(
                                field("accountId").description("계정 아이디"),
                                field("password").description("비밀번호"),
                                field("name").description("이름"),
                                field("phone").description("전화번호"),
                                field("mobile").description("휴대전화 번호"),
                                field("email").description("이메일"),
                                field("marketingAgreement").type(JsonFieldType.BOOLEAN).description("마케팅 수신 동의"),
                                field("code").description("인증코드")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("기관 사용자 회원 가입")
    void signUpWithOrganization() throws Exception {
        //given
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234", "한국고등학교");

        //when
        ResultActions actions = mockMvc.perform(post(AuthController.SIGN_UP_ORGANIZATION_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("sign-up-with-organization",
                        requestFields(
                                field("organizationName").description("기관명"),
                                field("accountId").description("계정 아이디"),
                                field("password").description("비밀번호"),
                                field("name").description("이름"),
                                field("phone").description("전화번호"),
                                field("mobile").description("휴대전화 번호"),
                                field("email").description("이메일"),
                                field("marketingAgreement").type(JsonFieldType.BOOLEAN).description("마케팅 수신 동의"),
                                field("code").description("인증코드")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인")
    void login() throws Exception {
        //given
        LoginDto requestDto = new LoginDto("abcd1234", "efgh1234");
        TokenDto.FullResponse responseDto = new TokenDto.FullResponse(AccessToken.from("access-token"), RefreshToken.from("refresh-token"));
        given(authService.login(requestDto)).willReturn(responseDto);

        //when
        ResultActions actions = mockMvc.perform(post(AuthController.LOGIN_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("auth-login",
                        requestFields(
                                field("accountId").description("아이디"),
                                field("password").description("비밀번호")
                        ),
                        responseFields(
                                field("accessToken").description("액세스 토큰"),
                                field("refreshToken").description("리프레시 토큰")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("로그아웃")
    void logout() throws Exception {
        //given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        //when
        ResultActions actions = mockMvc.perform(post(AuthController.LOGOUT_URI)
                .with(csrf())
                .header(Header.AUTH, accessToken)
                .header(Header.REFRESH, refreshToken));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("auth-logout",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰"),
                                header(Header.REFRESH).description("리프레시 토큰")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("토큰 재발급")
    void reissue() throws Exception {
        //given
        String accessToken = "old-access-token";
        String refreshToken = "refresh-token";
        given(reissueService.reissue(accessToken, refreshToken)).willReturn(new TokenDto.AccessResponse(AccessToken.from("new-access-token")));

        //when
        ResultActions actions = mockMvc.perform(post(AuthController.REISSUE_URI)
                .with(csrf())
                .header(Header.AUTH, accessToken)
                .header(Header.REFRESH, refreshToken));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("auth-reissue",
                        requestHeaders(
                                header(Header.AUTH).description("만료된 액세스 토큰"),
                                header(Header.REFRESH).description("리프레시 토큰")
                        ),
                        responseFields(
                                field("accessToken").description("갱신된 액세스 토큰")
                        )));
    }

}