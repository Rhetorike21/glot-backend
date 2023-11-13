package rhetorike.glot.domain._4order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    public final static String UNSUBSCRIBE_URI = "/api/subscribe/stop";

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(UNSUBSCRIBE_URI)
    public ResponseEntity<List<OrderDto.GetResponse>> unsubscribe(@AuthenticationPrincipal Long userId){
        subscriptionService.unsubscribe(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
