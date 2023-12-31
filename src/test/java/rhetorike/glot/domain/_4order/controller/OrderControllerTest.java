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
    @DisplayName("[요금제 주문]")
    void makeOrder() throws Exception {
        //given
        OrderDto.MakeRequest orderDto = new OrderDto.MakeRequest(1L, 1, new Payment("1234-1234-1234-1234", "2028-04", "990101", "01"));
        given(orderService.makeOrder(any(), any())).willReturn(new SingleParamDto<>("abcdefghi"));

        //when
        ResultActions actions = mockMvc.perform(post(OrderController.MAKE_ORDER_URI)
                .header(Header.AUTH, "access-token")
                .content(objectMapper.writeValueAsString(orderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("order-make",
                        requestHeaders(
                                HeaderElement.header(Header.AUTH).description("액세스 토큰")
                        ),
                        requestFields(
                                field("planId").description("플랜 아이디넘버").type(JsonFieldType.NUMBER),
                                field("quantity").description("수량").type(JsonFieldType.NUMBER),
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
        List<OrderDto.History> histories = List.of(new OrderDto.History(LocalDate.now(), "2023년 11월 1일 ~ 2023년 12월 1일", "****-****-****-1234", 1000L, 100L, "paid"));
        OrderDto.GetResponse responseBody = new OrderDto.GetResponse("GLOT 베이직", "구독 중", "KB국민카드", "13", "2023년 11월 13일 (토)", "2023.11월", histories);
        given(orderService.getPayInfo(any())).willReturn(responseBody);

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
                                field("plan").description("이용 중인 요금제 명"),
                                field("status").description("구독 상태 (구독 중 | 구독 정지 | null)"),
                                field("payMethod").description("구독 중인 요금제 명"),
                                field("payPeriod").description("구독 중인 요금제 명"),
                                field("nextPayDate").description("구독 중인 요금제 명"),
                                field("firstPaidDate").description("구독 중인 요금제 명"),
                                field("history[].paidDate").description("결제 일자"),
                                field("history[].duration").description("이용 가능 기간"),
                                field("history[].cardNumber").description("카드 번호"),
                                field("history[].amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                field("history[].surtax").type(JsonFieldType.NUMBER).description("부가세"),
                                field("history[].status").description("주문 상태(주문 완료 | 주문 취소 | 결제 실패 | 미결제)")
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