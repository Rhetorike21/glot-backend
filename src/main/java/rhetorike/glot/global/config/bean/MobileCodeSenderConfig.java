package rhetorike.glot.global.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rhetorike.glot.domain._1auth.entity.MobileCertCode;
import rhetorike.glot.domain._1auth.service.codesender.smssender.BasicSmsCodeSender;
import rhetorike.glot.domain._1auth.service.codesender.smssender.CoolMobileCodeSender;
import rhetorike.glot.domain._1auth.service.codesender.smssender.MobileCodeSender;

@Configuration
public class MobileCodeSenderConfig {

    @Bean
    @Profile({"local", "test", "inttest", "dev"})
    MobileCodeSender basicSmsSender() {
        return new BasicSmsCodeSender();
    }

    @Bean
    @Profile({"prod"})
    MobileCodeSender coolSmsSender(){
        return new CoolMobileCodeSender();
    }
}
