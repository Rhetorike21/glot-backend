package rhetorike.glot.domain._1auth.service.smscert.smssender;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.jetbrains.annotations.NotNull;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.global.error.exception.ConnectionFailedException;

@Slf4j
public class CoolSmsSender implements SmsSender{
    private static final String FROM = "054-475-1234";
    private static final String API_KEY = "api-key";
    private static final String SECRET_KEY = "secret-key";
    private static final String DOMAIN_URL = "https://api.coolsms.co.kr";
    @Override
    public void sendCertCode(String mobile, CertCode certCode) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, SECRET_KEY, DOMAIN_URL);
        Message message = createMessage(mobile, certCode.getPinNumbers());
        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            log.error("발송 실패 메시지 : {}", exception.getFailedMessageList());
            log.error(exception.getMessage());
            throw new ConnectionFailedException();
        } catch (Exception exception) {
            log.error(exception.getMessage());
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
