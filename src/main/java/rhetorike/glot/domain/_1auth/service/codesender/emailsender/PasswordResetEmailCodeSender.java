package rhetorike.glot.domain._1auth.service.codesender.emailsender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.entity.EmailCertCode;
import rhetorike.glot.global.error.exception.MailingFailedException;
import rhetorike.glot.global.util.email.Email;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetEmailCodeSender implements EmailCodeSender {

    private final JavaMailSender javaMailSender;
    private final static String CERT_CODE_AREA = "[CERT_CODE_AREA]";
    private final static String EMAIL_TITLE = "GLOT 비밀번호 변경 안내";
    private final static String EMAIL_CONTENT = getContent();

    /**
     * 이메일을 전송합니다.
     *
     * @param certCode 이메일 인증 코드
     */
    @Override
    public void send(EmailCertCode certCode) {
        try {
            MimeMessage mimeMessage = createMimeMessage(certCode);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailingFailedException();
        }
    }

    @NotNull
    private MimeMessage createMimeMessage(EmailCertCode certCode) throws MessagingException {
        String content = EMAIL_CONTENT.replace(CERT_CODE_AREA, certCode.getNumber());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(certCode.getEmail());
        mimeMessageHelper.setSubject(EMAIL_TITLE);
        mimeMessageHelper.setText(content, true);
        return mimeMessage;
    }

    private static String getContent(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "    <div style=\"font-family: Arial, sans-serif; width: 100%; height: 100vh; display: flex; flex-direction: column; align-items: center; background-color: #f2f3f5;\">\n" +
                "        <div style=\"width: 100%; height: 100%; background-color: white;\">\n" +
                "            <div style=\"background-color: #3290ff; color: white; padding: 40px; display: flex; flex-direction: column; align-items: left;\">\n" +
                "                <img src=\"https://i.postimg.cc/G3GBHJvP/GLOT-logo.png\" alt=\"로고\" style=\"width: 73px; height: 32px;\">\n" +
                "                <div style=\"font-size: 32px; font-weight: 700; text-align: left; margin-top: 50px;\">비밀번호 변경 안내</div>\n" +
                "                <div style=\"font-size: 16px; font-weight: 500; text-align: left; white-space: pre-line; line-height: 1.5\">\n" +
                "                    안녕하세요, 레토리케 입니다.\n" +
                "                    고객님의 비밀번호가 초기화 되었습니다. 아래의 링크로 접속하셔서\n" +
                "                    비밀번호를 재설정해주십시오.\n" +
                "                </div>\n" +
                "            </div>\n" +
                "             <div style=\"padding: 40px; background-color: white; display: flex; flex-direction: column; align-items: center;\">\n" +
                "                <div style=\"width: 90%; background-color: #f2f3f5; padding: 30px; border-radius: 8px; text-align: left;\">\n" +
                CERT_CODE_AREA +
                "                </div>\n" +
                "                <div style=\"width: 95%; height: 100px; text-align: left; padding-top: 20px;\">\n" +
                "                  위의 링크를 클릭해도 시작되지 않으면 URL을 복사하여 새 창에 붙여 넣어 주십시오.\n" +
                "                </div>\n" +
                "                <div style=\"border-top: 1px solid black; width: 95%; height: 100px; margin-top: 30px;\">\n" +
                "                  <p style=\"font-size: 14px; font-weight: 400\">Copyright.(c) RHETORIKE All rights reserved.</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
