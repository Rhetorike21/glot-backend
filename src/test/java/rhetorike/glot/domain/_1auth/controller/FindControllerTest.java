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

    @Test
    @WithMockUser
    @DisplayName("메일로 아이디 찾기")
    void findAccountIdByEmail() throws Exception {
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
    @DisplayName("휴대폰으로 아이디 찾기")
    void findAccountIdByMobile() throws Exception {
        //given
        AccountIdFindDto.MobileRequest requestDto = new AccountIdFindDto.MobileRequest("홍길동", "01012345678", "123456");

        //when
        ResultActions actions = mockMvc.perform(post(FindController.FIND_ACCOUNT_ID_BY_MOBILE)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("find-accountId-mobile",
                        requestFields(
                                field("name").description("이름"),
                                field("mobile").description("전화번호"),
                                field("code").description("인증코드")
                        )));
    }
}