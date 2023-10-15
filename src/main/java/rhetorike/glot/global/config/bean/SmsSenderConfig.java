package rhetorike.glot.global.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rhetorike.glot.domain._1auth.service.smscert.smssender.BasicSmsSender;
import rhetorike.glot.domain._1auth.service.smscert.smssender.CoolSmsSender;
import rhetorike.glot.domain._1auth.service.smscert.smssender.SmsSender;

@Configuration
public class SmsSenderConfig {

    @Bean
    @Profile({"local", "test", "inttest"})
    SmsSender basicSmsSender() {
        return new BasicSmsSender();
    }

    @Bean
    @Profile({"dev"})
    SmsSender coolSmsSender(){
        return new CoolSmsSender();
    }
}
