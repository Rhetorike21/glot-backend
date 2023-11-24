package rhetorike.glot.domain._1auth.service.codesender.smssender;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.MobileCertCode;
import rhetorike.glot.domain._1auth.service.codesender.CodeSender;

@Service
public class BasicSmsCodeSender implements MobileCodeSender {

    @Override
    public void send(MobileCertCode certCode) {

    }
}
