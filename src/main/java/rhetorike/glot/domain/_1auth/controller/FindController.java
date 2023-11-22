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
    public static final String FIND_ACCOUNT_ID_BY_MOBILE = "/api/find/account-id/mobile";

    private final AccountIdFindService accountIdFindService;

    @PostMapping(FIND_ACCOUNT_ID_BY_EMAIL)
    public ResponseEntity<Void> findAccountIdByEmail(@RequestBody AccountIdFindDto.EmailRequest requestDto) {
        accountIdFindService.findAccountIdByEmail(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(FIND_ACCOUNT_ID_BY_MOBILE)
    public ResponseEntity<AccountIdFindDto.MobileResponse> findAccountIdByMobile(@RequestBody AccountIdFindDto.MobileRequest requestDto) {
        AccountIdFindDto.MobileResponse responseBody = accountIdFindService.findAccountIdByMobile(requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
