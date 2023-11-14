package rhetorike.glot.domain._2user.controller;

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
import rhetorike.glot.domain._2user.dto.UserDto;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.service.UserService;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.HeaderElement.header;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@WebMvcTest(value = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @WithMockUser
    @DisplayName("사용자 프로필 조회")
    void getUserProfile() throws Exception {
        //given
        String accessToken = "access-token";
        UserProfileDto.GetRequest userInfo = new UserProfileDto.GetRequest("개인", "홍길동", "054-1234-5678", "010-1234-5678", "hong@naver.com", "hello123", "베이직 요금제", null);
        given(userService.getUserProfile(any())).willReturn(userInfo);

        //when
        ResultActions actions = mockMvc.perform(get(UserController.USER_PROFILE_GET_URI)
                .header(Header.AUTH, accessToken)
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("user-profile-get",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰")
                        ),
                        responseFields(
                                field("userType").description("고객 유형 (개인 | 기관 | null)"),
                                field("name").description("이름"),
                                field("phone").description("전화 번호"),
                                field("mobile").description("휴대 전화 번호"),
                                field("email").description("이메일"),
                                field("accountId").description("아이디"),
                                field("subscription").description("이용 중인 플랜").optional(),
                                field("orgName").description("기관명").optional()
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 정보 수정")
    void updateUserProfile() throws Exception {
        //given
        String accessToken = "access-token";
        UserProfileDto.UpdateParam requestBody = new UserProfileDto.UpdateParam("홍길동", "01012345678", null, "aaa1123");

        //when
        ResultActions actions = mockMvc.perform(patch(UserController.USER_PROFILE_UPDATE_URI)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .header(Header.AUTH, accessToken)
                .with(csrf()));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("user-profile-update",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("name").description("이름").optional(),
                                field("mobile").description("휴대 전화 번호").optional(),
                                field("email").description("이메일").optional(),
                                field("password").description("비밀번호").optional()
                        )));
    }
}