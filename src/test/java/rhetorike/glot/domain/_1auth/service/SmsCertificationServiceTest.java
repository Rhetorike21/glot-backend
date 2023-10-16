package rhetorike.glot.domain._1auth.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._1auth.service.smscert.SmsCertificationService;
import rhetorike.glot.domain._1auth.service.smscert.smssender.SmsSender;
import rhetorike.glot.global.util.RandomTextGenerator;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ServiceTest
class SmsCertificationServiceTest {

    @InjectMocks
    SmsCertificationService smsCertificationService;
    @Mock
    SmsSender smsSender;
    @Mock
    CertCodeRepository certCodeRepository;
    @Mock
    RandomTextGenerator randomTextGenerator;

    @Test
    @DisplayName("사용자의 전화번호로 인증코드를 전송한다.")
    void sendCode(){
        //given
        String pinNumbers = "1234";
        given(randomTextGenerator.generateFourNumbers()).willReturn(pinNumbers);
        given(certCodeRepository.findByPinNumbers(pinNumbers)).willReturn(Optional.empty());

        //when
        smsCertificationService.sendCertCode("01012345678");

        //then
        verify(randomTextGenerator).generateFourNumbers();
        verify(certCodeRepository).findByPinNumbers(pinNumbers);
    }


    @Test
    @DisplayName("현재까지 발급되지 않은 인증코드를 전송한다.")
    void sendUniquePin(){
        //given
        given(randomTextGenerator.generateFourNumbers()).willReturn("1234").willReturn("5678").willReturn("9012");
        given(certCodeRepository.findByPinNumbers("1234")).willReturn(Optional.of(new CertCode("1234", false)));
        given(certCodeRepository.findByPinNumbers("5678")).willReturn(Optional.of(new CertCode("5678", false)));
        given(certCodeRepository.findByPinNumbers("9012")).willReturn(Optional.empty());

        //when
        smsCertificationService.sendCertCode("01012345678");

        //then
        verify(randomTextGenerator, times(3)).generateFourNumbers();
        verify(certCodeRepository, times(3)).findByPinNumbers(any());
    }


    @Test
    @DisplayName("인증 번호를 확인한다.")
    void verifyCode(){
        //given
        String pinNumbers = "1234";
        given(certCodeRepository.findByPinNumbers(pinNumbers)).willReturn(Optional.of(new CertCode("1234", false)));

        //when
        boolean result = smsCertificationService.verifyCode(pinNumbers);

        //then
        verify(certCodeRepository).findByPinNumbers(pinNumbers);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("전송된 인증번호가 없는 경우 false를 리턴한다.")
    void verifyCodeFailed(){
        //given
        String pinNumbers = "1234";
        given(certCodeRepository.findByPinNumbers(pinNumbers)).willReturn(Optional.empty());

        //when
        boolean result = smsCertificationService.verifyCode(pinNumbers);

        //then
        verify(certCodeRepository).findByPinNumbers(pinNumbers);
        Assertions.assertThat(result).isFalse();
    }


}