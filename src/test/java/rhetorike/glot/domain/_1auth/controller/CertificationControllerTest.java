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
import rhetorike.glot.domain._1auth.dto.CertificationDto;
import rhetorike.glot.domain._1auth.service.CertificationService;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsQueryParam.queryParams;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.ParamElement.param;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = CertificationController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class CertificationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CertificationService certificationService;

    @Test
    @WithMockUser
    @DisplayName("sms 인증 코드 전송")
    void sendCodeBySms() throws Exception {
        //given
        CertificationDto.CodeRequest requestDto = new CertificationDto.CodeRequest("01012345678");

        //when
        ResultActions actions = mockMvc.perform(post(CertificationController.SEND_CODE_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("cert-sms-code",
                        requestFields(
                                field("mobile").description("전화번호")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("sms 인증 코드 확인")
    void verifyCodeBySms() throws Exception {
        //given
        String code = "1234";
        given(certificationService.isValidNumber(code)).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(post(CertificationController.VERIFY_CODE_URI)
                .with(csrf())
                .queryParam("code", "1234"));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("cert-sms-verify",
                        queryParams(
                                param("code").description("인증코드")
                        ),
                        responseFields(
                                field("success").type(JsonFieldType.BOOLEAN).description("인증 여부")
                        )));
    }
}