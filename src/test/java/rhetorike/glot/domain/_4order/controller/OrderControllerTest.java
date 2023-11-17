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
import rhetorike.glot.domain._4order.service.RefundService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.time.LocalDate;
import java.util.List;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    @DisplayName("[베이직 요금제 주문]")
    void makeOrder() throws Exception {
        //given
        OrderDto.BasicOrderRequest orderDto = new OrderDto.BasicOrderRequest(PlanPeriod.MONTH.getName(), new Payment("1234-1234-1234-1234", "2028-04", "990101", "01"));
        given(orderService.makeBasicOrder(any(), any())).willReturn(new SingleParamDto<>("abcdefghi"));

        //when
        ResultActions actions = mockMvc.perform(post(OrderController.MAKE_BASIC_ORDER_URI)
                .header(Header.AUTH, "access-token")
                .content(objectMapper.writeValueAsString(orderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("order-basic",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("planPeriod").description("기간 (1m | 1y)"),
                                field("payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                field("payment.cardNumber").description("카드 번호(NNNN-NNNN-NNNN-NNNN)"),
                                field("payment.expiry").description("카드 유효기간(YYYY-MM)"),
                                field("payment.birthDate").description("생년월일(YYMMDD)"),
                                field("payment.password").description("비밀번호 앞 두자리(XX)")
                        ),
                        responseFields(
                                field("data").description("주문 번호")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[엔터프라이즈 요금제 주문]")
    void makeEnterpriseOrder() throws Exception {
        //given
        OrderDto.EnterpriseOrderRequest orderDto = new OrderDto.EnterpriseOrderRequest(PlanPeriod.MONTH.getName(), 3, new Payment("1234-1234-1234-1234", "2028-04", "990101", "01"));
        given(orderService.makeEnterpriseOrder(any(), any())).willReturn(new SingleParamDto<>("abcdefghi"));

        //when
        ResultActions actions = mockMvc.perform(post(OrderController.MAKE_ENTERPRISE_ORDER_URI)
                .header(Header.AUTH, "access-token")
                .content(objectMapper.writeValueAsString(orderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("order-enterprise",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("planPeriod").description("기간 (1m | 1y)"),
                                field("quantity").type(JsonFieldType.NUMBER).description("주문 수량"),
                                field("payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                field("payment.cardNumber").description("카드 번호(NNNN-NNNN-NNNN-NNNN)"),
                                field("payment.expiry").description("카드 유효기간(YYYY-MM)"),
                                field("payment.birthDate").description("생년월일(YYMMDD)"),
                                field("payment.password").description("비밀번호 앞 두자리(XX)")
                        ),
                        responseFields(
                                field("data").description("주문 번호")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[주문 내역 조회]")
    void getOrders() throws Exception {
        //given
        List<OrderDto.GetResponse> responseBody = List.of(new OrderDto.GetResponse(LocalDate.now(), "2023년 11월 1일 ~ 2023년 12월 1일", "****-****-****-1234", 1000L, 100L, "paid"));
        given(orderService.getOrders(any())).willReturn(responseBody);

        //when
        ResultActions actions = mockMvc.perform(get(OrderController.GET_ORDER_URI)
                .header(Header.AUTH, "access-token")
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("order-get",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        responseFields(
                                field("[].payDate").description("결제 일자"),
                                field("[].duration").description("이용 가능 기간"),
                                field("[].cardNumber").description("카드 번호"),
                                field("[].amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                field("[].surtax").type(JsonFieldType.NUMBER).description("부가세"),
                                field("[].status").description("주문 상태(주문 완료 | 주문 취소 | 결제 실패 | 미결제)")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[지불 방식 변경]")
    void changePayMethod() throws Exception {
        //given
        Payment payment = new Payment("1234-1234-1234-1234", "2028-04", "990101", "01");

        //when
        ResultActions actions = mockMvc.perform(patch(OrderController.CHANGE_PAY_METHOD_URI)
                .header(Header.AUTH, "access-token")
                .content(objectMapper.writeValueAsString(payment))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("payment-update",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("cardNumber").description("카드 번호(NNNN-NNNN-NNNN-NNNN)"),
                                field("expiry").description("카드 유효기간(YYYY-MM)"),
                                field("birthDate").description("생년월일(YYMMDD)"),
                                field("password").description("비밀번호 앞 두자리(XX)")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[환불]")
    void refund() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(post(OrderController.REFUND_URI)
                .header(Header.AUTH, "access-token")
                .with(csrf()));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("order-refund",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[환불 정보 조회]")
    void getRefundInfo() throws Exception {
        //given
        given(orderService.getRefundInfo(any())).willReturn(new OrderDto.RefundResponse("abcd1234", 25, 13, 9205));

        //when
        ResultActions actions = mockMvc.perform(get(OrderController.REFUND_INFO_URI)
                .header(Header.AUTH, "access-token")
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("order-refund-info",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        responseFields(
                                field("accountId").description("내 계정"),
                                field("numOfMembers").description("구매 계정 수").type(JsonFieldType.NUMBER),
                                field("remainDays").description("잔여 구독일 수").type(JsonFieldType.NUMBER),
                                field("refundAmount").description("환불 예정 금액").type(JsonFieldType.NUMBER)
                        )));
    }
}