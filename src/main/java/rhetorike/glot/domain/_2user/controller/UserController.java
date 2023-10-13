package rhetorike.glot.domain._2user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._2user.dto.UserInfo;
import rhetorike.glot.domain._2user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    public final static String USER_INFO_URI = "/api/user/info";
    private final UserService userService;

    @GetMapping(USER_INFO_URI)
    public ResponseEntity<UserInfo> getUserInfo(@AuthenticationPrincipal Long userId){
        UserInfo responseBody = userService.getUserInfo(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
