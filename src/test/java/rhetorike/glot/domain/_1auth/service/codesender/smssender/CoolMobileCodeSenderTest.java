package rhetorike.glot.domain._1auth.service.codesender.smssender;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._1auth.entity.MobileCertCode;
import rhetorike.glot.global.error.exception.ConnectionFailedException;
import rhetorike.glot.setup.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@Disabled
class CoolMobileCodeSenderTest extends IntegrationTest {
    @Autowired
    CoolMobileCodeSender coolMobileCodeSender;

    @Test
    @DisplayName("")
    void sendMessage(){
        //given

        //when
        coolMobileCodeSender.send(new MobileCertCode("0000", "01076078701"));

        //then


    }





}