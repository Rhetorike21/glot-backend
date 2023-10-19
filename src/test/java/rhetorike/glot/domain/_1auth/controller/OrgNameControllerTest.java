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
import rhetorike.glot.domain._1auth.dto.OrgNameDto;
import rhetorike.glot.domain._1auth.service.AuthService;
import rhetorike.glot.domain._1auth.service.orgnamesearch.OrgNameService;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;

import java.util.List;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsQueryParam.queryParams;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.ParamElement.param;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = OrgNameController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class OrgNameControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrgNameService orgNameService;


    @Test
    @WithMockUser
    @DisplayName("기관명 검색")
    void searchName() throws Exception {
        //given
        String keyword = "한국";
        given(orgNameService.searchName(keyword)).willReturn(List.of("한국초등학교", "한국중학교", "한국고등학교"));

        //when
        ResultActions actions = mockMvc.perform(post(OrgNameController.SEARCH_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(new OrgNameDto(keyword)))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("org-name-search",
                        requestFields(
                                field("keyword").description("검색어")
                        )));
    }
}