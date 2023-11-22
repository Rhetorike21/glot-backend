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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.service.PasswordResetService;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = ResetController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class ResetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PasswordResetService passwordResetService;

    @Test
    @WithMockUser
    @DisplayName("[이메일로 재설정 링크 전송]")
    void sendResetLinkByEmail() throws Exception {
        //given
        PasswordResetDto.LinkRequest requestDto = new PasswordResetDto.LinkRequest("abcd1234", "홍길동", "hong@naver.com");

        //when
        ResultActions actions = mockMvc.perform(post(ResetController.SEND_RESET_LINK_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("find-password-email",
                        requestFields(
                                field("accountId").description("아이디"),
                                field("name").description("이름"),
                                field("email").description("이메일")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[이메일/전화번호로 비밀번호 재설정]")
    void resetPassword() throws Exception {
        //given
        PasswordResetDto.Request requestDto = new PasswordResetDto.Request("abcd1234", "code", "new-password");

        //when
        ResultActions actions = mockMvc.perform(post(ResetController.SEND_RESET_LINK_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("reset-password",
                        requestFields(
                                field("accountId").description("아이디 (재설정 링크의 \"id\" 파라미터 값)"),
                                field("code").description("인증 코드 (재설정 링크의 \"code\" 파라미터 값)"),
                                field("password").description("새 비밀번호")
                        )));
    }
}