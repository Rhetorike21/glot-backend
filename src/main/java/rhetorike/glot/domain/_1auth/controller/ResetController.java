package rhetorike.glot.domain._1auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.service.PasswordResetService;

@RestController
@RequiredArgsConstructor
public class ResetController {

    public static final String SEND_RESET_LINK_URI = "/api/reset/password/email";
    public static final String RESET_PASSWORD_URI = "/api/reset/password";

    private final PasswordResetService passwordResetService;

    @PostMapping(SEND_RESET_LINK_URI)
    public ResponseEntity<Void> sendResetLinkByEmail(@RequestBody PasswordResetDto.LinkRequest requestDto) {
        passwordResetService.sendResetLinkByEmail(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(RESET_PASSWORD_URI)
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDto.Request requestDto) {
        passwordResetService.resetPassword(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
