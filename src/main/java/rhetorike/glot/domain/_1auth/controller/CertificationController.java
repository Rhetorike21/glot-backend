package rhetorike.glot.domain._1auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._1auth.dto.CertificationDto;
import rhetorike.glot.domain._1auth.service.CertificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CertificationController {
    public static final String SEND_CODE_URI = "/api/cert/sms/code";
    public static final String VERIFY_CODE_URI = "/api/cert/sms/verify";
    private final CertificationService certificationService;

    @PostMapping(SEND_CODE_URI)
    public ResponseEntity<Void> sendCodeBySms(@RequestBody CertificationDto.CodeRequest requestDto){
        certificationService.sendMobileCode(requestDto.getMobile());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(VERIFY_CODE_URI)
    public ResponseEntity<CertificationDto.VerifyResponse> verifyCodeBySms(@RequestParam String code){
        boolean success = certificationService.isValidNumber(code);
        return new ResponseEntity<>(new CertificationDto.VerifyResponse(success), HttpStatus.OK);
    }
}
