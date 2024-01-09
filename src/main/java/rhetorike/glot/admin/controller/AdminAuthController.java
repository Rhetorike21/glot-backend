package rhetorike.glot.admin.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.admin.service.AdminAuthService;
import rhetorike.glot.domain._1auth.dto.LoginDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    public static final String LOGIN_URI = "/api/admin/login";

    private final AdminAuthService authService;

    @PermitAll
    @PostMapping(LOGIN_URI)
    public ResponseEntity<LoginDto.Response> login(@RequestBody LoginDto.Request requestDto) {
        LoginDto.Response responseBody = authService.login(requestDto);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}