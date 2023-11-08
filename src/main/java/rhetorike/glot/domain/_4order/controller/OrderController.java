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

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    public final static String MAKE_ORDER_URI = "/api/orders";
    public final static String GET_ORDER_URI = "/api/orders";
    public final static String CHANGE_PAY_METHOD_URI = "/api/payments";
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(MAKE_ORDER_URI)
    public ResponseEntity<Void> makeOrder(@RequestBody OrderDto.MakeRequest requestDto, @AuthenticationPrincipal Long userId){
        orderService.makeOrder(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(GET_ORDER_URI)
    public ResponseEntity<List<OrderDto.GetResponse>> getOrders(@AuthenticationPrincipal Long userId){
        List<OrderDto.GetResponse> responseBody = orderService.getOrders(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping(CHANGE_PAY_METHOD_URI)
    public ResponseEntity<List<OrderDto.GetResponse>> changePayMethod(@RequestBody Payment payment, @AuthenticationPrincipal Long userId){
        orderService.changePayMethod(payment, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
