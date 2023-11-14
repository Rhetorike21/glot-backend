package rhetorike.glot.domain._2user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._2user.dto.UserDto;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    public final static String USER_PROFILE_GET_URI = "/api/user/info";
    public final static String USER_PROFILE_UPDATE_URI = "/api/user/info";
    public final static String ACTIVATE_MEMBER = "/api/user/activate";
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping(USER_PROFILE_GET_URI)
    public ResponseEntity<UserProfileDto.GetRequest> getUserProfile(@AuthenticationPrincipal Long userId){
        UserProfileDto.GetRequest responseBody = userService.getUserProfile(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping(USER_PROFILE_UPDATE_URI)
    public ResponseEntity<Void> updateUserProfile(@RequestBody UserProfileDto.UpdateParam requestDto, @AuthenticationPrincipal Long userId){
        userService.updateUserProfile(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ORG')")
    @PostMapping(ACTIVATE_MEMBER)
    public ResponseEntity<Void> activateMember(@AuthenticationPrincipal Long userId, @RequestBody UserDto.ActivateRequest requestDto){
        userService.activate(userId, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
