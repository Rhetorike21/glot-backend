package rhetorike.glot.global.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rhetorike.glot.global.error.exception.MailingFailedException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * 이메일을 전송합니다.
     *
     * @param email 이메일
     */
    public void sendMail(Email email) {
        try {
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
