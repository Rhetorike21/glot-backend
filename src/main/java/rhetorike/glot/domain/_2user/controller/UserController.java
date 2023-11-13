package rhetorike.glot.domain._2user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    public final static String USER_PROFILE_GET_URI = "/api/user/info";
    public final static String USER_PROFILE_UPDATE_URI = "/api/user/info";
    private final UserService userService;

    @GetMapping(USER_PROFILE_GET_URI)
    public ResponseEntity<UserProfileDto.GetRequest> getUserProfile(@AuthenticationPrincipal Long userId){
        UserProfileDto.GetRequest responseBody = userService.getUserProfile(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PatchMapping(USER_PROFILE_UPDATE_URI)
    public ResponseEntity<Void> updateUserProfile(@RequestBody UserProfileDto.UpdateRequest requestDto, @AuthenticationPrincipal Long userId){
        userService.updateUserProfile(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
