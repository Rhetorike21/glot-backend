package rhetorike.glot.domain._1auth.service.codesender.smssender;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.MobileCertCode;
import rhetorike.glot.global.error.exception.ConnectionFailedException;

@Slf4j
@Service
public class CoolMobileCodeSender implements MobileCodeSender {

    @Value("${api.cool-sms.from-mobile}")
    private String FROM;
    @Value("${api.cool-sms.api-key}")
    private String API_KEY;
    @Value("${api.cool-sms.secret-key}")
    private String SECRET_KEY;
    private static final String DOMAIN_URL = "https://api.coolsms.co.kr";

    @Override
    public void send(MobileCertCode certCode) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, SECRET_KEY, DOMAIN_URL);
        Message message = createMessage(certCode.getMobile(), certCode.getNumber());
        try {
            messageService.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConnectionFailedException();
        }
    }

    @NotNull
    private Message createMessage(String mobile, String pinNumbers) {
        Message message = new Message();
        message.setFrom(FROM);
        message.setTo(mobile);
        message.setText("인증번호는 다음과 같습니다. " + pinNumbers);
        return message;
    }
}
