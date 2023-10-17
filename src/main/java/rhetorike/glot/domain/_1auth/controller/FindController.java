package rhetorike.glot.domain._1auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.service.AccountIdFindService;
import rhetorike.glot.domain._1auth.service.PasswordResetService;

@RestController
@RequiredArgsConstructor
public class FindController {

    public static final String FIND_ACCOUNT_ID_BY_EMAIL = "/api/find/account-id/email";
    public static final String FIND_PASSWORD_BY_EMAIL = "/api/find/password/email";
    public static final String RESET_PASSWORD = "/api/reset/password";

    private final AccountIdFindService accountIdFindService;
    private final PasswordResetService passwordResetService;

    @PostMapping(FIND_ACCOUNT_ID_BY_EMAIL)
    public ResponseEntity<Void> findAccountIdByEmail(@RequestBody AccountIdFindDto.EmailRequest requestDto) {
        accountIdFindService.sendAccountId(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(FIND_PASSWORD_BY_EMAIL)
    public ResponseEntity<Void> findPasswordByEmail(@RequestBody PasswordResetDto.EmailRequest requestDto) {
        passwordResetService.sendResetLink(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDto.ResetRequest requestDto) {
        passwordResetService.resetPassword(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
