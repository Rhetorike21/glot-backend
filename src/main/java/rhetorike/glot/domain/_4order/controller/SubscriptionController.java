package rhetorike.glot.domain._4order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._4order.dto.SubscriptionDto;
import rhetorike.glot.domain._4order.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    public final static String UNSUBSCRIBE_URI = "/api/subscription/stop";
    public final static String GET_SUBS_MEMBER_URI = "/api/subscription/members";
    public final static String UPDATE_SUBS_MEMBER_URI = "/api/subscription/members";

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasAnyRole('PERSONAL', 'ORG')")
    @DeleteMapping(UNSUBSCRIBE_URI)
    public ResponseEntity<Void> unsubscribe(@AuthenticationPrincipal Long userId){
        subscriptionService.unsubscribe(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ORG')")
    @GetMapping(GET_SUBS_MEMBER_URI)
    public ResponseEntity<List<SubscriptionDto.MemberResponse>> getSubscriptionMembers(@AuthenticationPrincipal Long userId){
        List<SubscriptionDto.MemberResponse> responseBody = subscriptionService.getSubscriptionMembers(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ORG')")
    @PatchMapping(UPDATE_SUBS_MEMBER_URI)
    public ResponseEntity<Void> updateSubscriptionMembers(@AuthenticationPrincipal Long userId, @RequestBody SubscriptionDto.MemberUpdateRequest requestDto){
        subscriptionService.updateSubscriptionMembers(userId, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
