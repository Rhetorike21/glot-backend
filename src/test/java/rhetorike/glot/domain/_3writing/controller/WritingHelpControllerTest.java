package rhetorike.glot.domain._3writing.controller;

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
import rhetorike.glot.domain._3writing.dto.WritingHelpDto;
import rhetorike.glot.domain._3writing.service.WritingHelpService;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;
import rhetorike.glot.global.util.dto.SingleParamDto;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.HeaderElement.header;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = WritingHelpController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class WritingHelpControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WritingHelpService writingHelpService;


    @Test
    @WithMockUser
    @DisplayName("[AI 작문 추천]")
    void createBoard() throws Exception {
        //given
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request("나는 바보 아니다.", "progress");
        given(writingHelpService.write(requestDto)).willReturn(new SingleParamDto<>("AI 작문 결과"));

        //when
        ResultActions actions = mockMvc.perform(post(WritingHelpController.WRITING_HELP_API)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("help-writing",
                        requestFields(
                                field("sentence").description("입력 문장"),
                                field("type").description("유형 (progress | reverse | conclusion)")
                        ),
                        responseFields(
                                field("data").description("AI 작문 결과")
                        )));
    }
}