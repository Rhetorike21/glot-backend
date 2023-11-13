package rhetorike.glot.domain._4order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hansol.restdocsdsl.element.HeaderElement;
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
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.SubscriptionService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;
import rhetorike.glot.global.util.dto.SingleParamDto;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@WebMvcTest(value = SubscriptionController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class SubscriptionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SubscriptionService subscriptionService;

    @Test
    @WithMockUser
    @DisplayName("[구독 취소]")
    void unsubscribe() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(delete(SubscriptionController.UNSUBSCRIBE_URI)
                .header(Header.AUTH, "access-token")
                .with(csrf()));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("subscription-stop",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        )));
    }
}