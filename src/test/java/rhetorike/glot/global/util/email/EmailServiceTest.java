package rhetorike.glot.global.util.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    @Disabled
    @DisplayName("메일을 전송한다.(확인완료)")
    void sendMail() {
        //given
        String email = "hansol8701@naver.com";
        String accountId = "abc1234";

        //when
        emailService.sendMail(Email.newAccountIdEmail(email, List.of("abc1234", "def5678", "ghi9012")));

        //then


    }


}