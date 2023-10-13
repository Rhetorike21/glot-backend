package rhetorike.glot.domain._1auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @GetMapping("/api/test")
    public ResponseEntity<Void> testMethod(@AuthenticationPrincipal Long userId){
        log.info("hi {}", userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
