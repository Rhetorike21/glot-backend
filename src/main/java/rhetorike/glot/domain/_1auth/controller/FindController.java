package rhetorike.glot.domain._1auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.service.AccountIdFindService;

@RestController
@RequiredArgsConstructor
public class FindController {

    public static final String FIND_ACCOUNT_ID_BY_EMAIL = "/api/find/account-id/email";

    private final AccountIdFindService accountIdFindService;

    @PostMapping(FIND_ACCOUNT_ID_BY_EMAIL)
    public ResponseEntity<Void> sendMail(@RequestBody AccountIdFindDto.EmailRequest requestDto) {
        accountIdFindService.findByEmail(requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
