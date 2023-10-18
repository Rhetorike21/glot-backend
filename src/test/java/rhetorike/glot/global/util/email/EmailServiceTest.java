package rhetorike.glot.global.util.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rhetorike.glot.domain._1auth.entity.ResetCode;

import java.util.List;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    @Disabled
    @DisplayName("아이디 찾기 메일을 전송한다.(확인완료)")
    void sendAccountIdEmail() {
        //given
        String email = "hansol8701@naver.com";

        //when
        emailService.sendMail(Email.newAccountIdEmail(email, List.of("abc1234", "def5678", "ghi9012")));

        //then


    }

    @Test
    @Disabled
    @DisplayName("비밀번호 재설정 메일을 전송한다.(확인완료)")
    void sendPasswordResetMail() {
        //given
        String email = "hansol8701@naver.com";
        String accountId = "abc1234";

        //when
        emailService.sendMail(Email.newPasswordResetEmail(email, ResetCode.randomResetCode(accountId)));

        //then

    }


}