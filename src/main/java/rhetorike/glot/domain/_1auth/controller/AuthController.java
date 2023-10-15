package rhetorike.glot.domain._1auth.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.SignUpRequest;
import rhetorike.glot.domain._1auth.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    public final static String SIGN_UP_PERSONAL_URI = "/api/sign-up/personal";
    public final static String SIGN_UP_ORGANIZATION_URI = "/api/sign-up/organization";
    private final AuthService authService;
    @PermitAll
    @PostMapping(SIGN_UP_PERSONAL_URI)
    public ResponseEntity<Void> signUpWithPersonal(@RequestBody SignUpRequest.PersonalDto requestDto){
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @PostMapping(SIGN_UP_ORGANIZATION_URI)
    public ResponseEntity<Void> signUpWithOrganization(@RequestBody SignUpRequest.OrganizationDto requestDto){
        authService.signUp(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
