package rhetorike.glot.domain._1auth.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.service.AuthService;
import rhetorike.glot.domain._1auth.service.ReissueService;
import rhetorike.glot.global.constant.Header;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    public final static String SIGN_UP_PERSONAL_URI = "/api/auth/sign-up/personal";
    public final static String SIGN_UP_ORGANIZATION_URI = "/api/auth/sign-up/org";
    public final static String LOGIN_URI = "/api/auth/login";
    public final static String LOGOUT_URI = "/api/auth/logout";
    public final static String WITHDRAW_URI = "/api/auth/withdraw";
    public final static String REISSUE_URI = "/api/auth/reissue";
    private final AuthService authService;
    private final ReissueService reissueService;

    @PermitAll
    @PostMapping(SIGN_UP_PERSONAL_URI)
    public ResponseEntity<Void> signUpWithPersonal(@RequestBody SignUpDto.PersonalRequest requestDto) {
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(SIGN_UP_ORGANIZATION_URI)
    public ResponseEntity<Void> signUpWithOrg(@RequestBody SignUpDto.OrgRequest requestDto) {
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(LOGIN_URI)
    public ResponseEntity<TokenDto.FullResponse> login(@RequestBody LoginDto requestDto) {
        TokenDto.FullResponse responseBody = authService.login(requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(LOGOUT_URI)
    public ResponseEntity<Void> logout(@RequestHeader(Header.AUTH) String accessToken, @RequestHeader(Header.REFRESH) String refreshToken) {
        authService.logout(accessToken, refreshToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(WITHDRAW_URI)
    public ResponseEntity<Void> withdraw(@RequestHeader(Header.AUTH) String accessToken, @RequestHeader(Header.REFRESH) String refreshToken) {
        authService.withdraw(accessToken, refreshToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(REISSUE_URI)
    public ResponseEntity<TokenDto.AccessResponse> reissue(@RequestHeader(Header.AUTH) String accessToken, @RequestHeader(Header.REFRESH) String refreshToken) {
        TokenDto.AccessResponse responseBody = reissueService.reissue(accessToken, refreshToken);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
