package rhetorike.glot.domain._4order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    public final static String MAKE_ORDER_URI = "/api/orders";
    public final static String GET_ORDER_URI = "/api/orders";
    public final static String CHANGE_PAY_METHOD_URI = "/api/orders/payments";
    public final static String REFUND_URI = "/api/orders/refund";
    public final static String REFUND_INFO_URI = "/api/orders/refund";
    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @PostMapping(MAKE_ORDER_URI)
    public ResponseEntity<SingleParamDto<String>> makeOrder(@RequestBody OrderDto.MakeRequest requestDto, @AuthenticationPrincipal Long userId) {
        SingleParamDto<String> responseBody = orderService.makeOrder(requestDto, userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @GetMapping(GET_ORDER_URI)
    public ResponseEntity<OrderDto.GetResponse> getOrders(@AuthenticationPrincipal Long userId) {
        OrderDto.GetResponse responseBody = orderService.getPayInfo(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @PatchMapping(CHANGE_PAY_METHOD_URI)
    public ResponseEntity<List<OrderDto.History>> changePayMethod(@RequestBody Payment payment, @AuthenticationPrincipal Long userId) {
        orderService.changePayMethod(payment, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @PostMapping(REFUND_URI)
    public ResponseEntity<List<OrderDto.History>> refund(@AuthenticationPrincipal Long userId) {
        orderService.refund(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @GetMapping(REFUND_INFO_URI)
    public ResponseEntity<OrderDto.RefundResponse> getRefundInfo(@AuthenticationPrincipal Long userId) {
        OrderDto.RefundResponse responseBody = orderService.getRefundInfo(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
