package rhetorike.glot.domain._1auth.service.codesender.emailsender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.EmailCertCode;
import rhetorike.glot.global.error.exception.MailingFailedException;
import rhetorike.glot.global.util.email.Email;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetEmailCodeSender implements EmailCodeSender {

    private final JavaMailSender javaMailSender;

    /**
     * 이메일을 전송합니다.
     *
     * @param certCode 이메일 인증 코드
     */
    @Override
    public void send(EmailCertCode certCode) {
        try {
            Email email = Email.newPasswordResetEmail(certCode);
            MimeMessage mimeMessage = createMimeMessage(email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailingFailedException();
        }
    }

    @NotNull
    private MimeMessage createMimeMessage(Email email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(email.getDestination());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setText(email.getContent(), true);
        return mimeMessage;
    }
}
