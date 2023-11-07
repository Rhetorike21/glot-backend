package rhetorike.glot.domain._4order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.vo.Payment;

@RestController
@RequiredArgsConstructor
public class OrderController {

    public final static String MAKE_ORDER_URI = "/api/orders";
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(MAKE_ORDER_URI)
    public ResponseEntity<Void> makeOrder(@RequestBody OrderDto requestDto, @AuthenticationPrincipal Long userId){
        orderService.makeOrder(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
