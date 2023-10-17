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
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.service.AccountIdFindService;
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
@WebMvcTest(value = FindController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class FindControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccountIdFindService accountIdFindService;
    @MockBean
    PasswordResetService passwordResetService;


    @Test
    @WithMockUser
    @DisplayName("아이디 정보가 담긴 메일을 발송한다.")
    void sendMail() throws Exception {
        //given
        AccountIdFindDto.EmailRequest requestDto = new AccountIdFindDto.EmailRequest("홍길동", "hong@naver.com");

        //when
        ResultActions actions = mockMvc.perform(post(FindController.FIND_ACCOUNT_ID_BY_EMAIL)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("find-accountId-email",
                        requestFields(
                                field("name").description("이름"),
                                field("email").description("이메일")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 재설정 링크가 담긴 메일을 발송한다.")
    void sendResetLink() throws Exception {
        //given
        PasswordResetDto.EmailRequest requestDto = new PasswordResetDto.EmailRequest("abcd1234", "홍길동", "hong@naver.com");

        //when
        ResultActions actions = mockMvc.perform(post(FindController.FIND_PASSWORD_BY_EMAIL)
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
    @DisplayName("비밀번호를 재설정한다.")
    void resetPassword() throws Exception {
        //given
        PasswordResetDto.ResetRequest requestDto = new PasswordResetDto.ResetRequest("abcd1234", "code", "new-password");

        //when
        ResultActions actions = mockMvc.perform(post(FindController.RESET_PASSWORD)
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