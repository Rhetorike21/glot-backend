package rhetorike.glot.domain._1auth.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    public final static String SIGN_UP_PERSONAL_URI = "/api/auth/sign-up/personal";
    public final static String SIGN_UP_ORGANIZATION_URI = "/api/auth/sign-up/org";
    public final static String LOGIN_URI = "/api/auth/login";
    private final AuthService authService;
    @PermitAll
    @PostMapping(SIGN_UP_PERSONAL_URI)
    public ResponseEntity<Void> signUpWithPersonal(@RequestBody SignUpDto.PersonalRequest requestDto){
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(SIGN_UP_ORGANIZATION_URI)
    public ResponseEntity<Void> signUpWithOrganization(@RequestBody SignUpDto.OrgRequest requestDto){
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(LOGIN_URI)
    public ResponseEntity<TokenDto.FullResponse> login(@RequestBody LoginDto requestDto){
        TokenDto.FullResponse responseBody =  authService.login(requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
