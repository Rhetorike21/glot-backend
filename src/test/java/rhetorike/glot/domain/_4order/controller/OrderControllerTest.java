package rhetorike.glot.domain._4order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hansol.restdocsdsl.element.FieldElement;
import hansol.restdocsdsl.element.HeaderElement;
import lombok.With;
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
import rhetorike.glot.domain._1auth.service.AuthService;
import rhetorike.glot.domain._1auth.service.ReissueService;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsPathParam.pathParams;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.ParamElement.param;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = OrderController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;



    @Test
    @WithMockUser
    @DisplayName("[상품 주문]")
    void makeOrder() throws Exception {
        //given
        OrderDto orderDto = new OrderDto(1L, 1, new Payment("1234-1234-1234-1234", "2028-04", "990101", "01"));

        //when
        ResultActions actions = mockMvc.perform(post(OrderController.MAKE_ORDER_URI, 1L)
                .header(Header.AUTH, "access-token")
                .content(objectMapper.writeValueAsString(orderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("order-make",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("planId").type(JsonFieldType.NUMBER).description("상품 아이디넘버"),
                                field("quantity").type(JsonFieldType.NUMBER).description("주문 수량 (베이직 플랜의 경우 입력 값과 무관하게 1개만 구매됩니다)"),
                                field("payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                field("payment.cardNumber").description("카드 번호(NNNN-NNNN-NNNN-NNNN)"),
                                field("payment.expiry").description("카드 유효기간(YYYY-MM)"),
                                field("payment.birthDate").description("생년월일(YYMMDD)"),
                                field("payment.password").description("비밀번호 앞 두자리(XX)")
                        )));
    }
}