package rhetorike.glot.domain._1auth.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._1auth.service.codesender.smssender.MobileCodeSender;
import rhetorike.glot.global.util.RandomTextGenerator;
import rhetorike.glot.setup.ServiceTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ServiceTest
class SmsCertificationServiceTest {

    @InjectMocks
    CertificationService certificationService;
    @Mock
    MobileCodeSender mobileCodeSender;
    @Mock
    CertCodeRepository certCodeRepository;
    @Mock
    RandomTextGenerator randomTextGenerator;

    @Test
    @DisplayName("사용자의 전화번호로 인증코드를 전송한다.")
    void sendCode(){
        //given
        String pinNumbers = "123456";
        given(randomTextGenerator.generateSixNumbers()).willReturn(pinNumbers);
        given(certCodeRepository.doesExists(pinNumbers)).willReturn(false);

        //when
        certificationService.sendMobileCode("01012345678");

        //then
        verify(randomTextGenerator).generateSixNumbers();
        verify(certCodeRepository).doesExists(pinNumbers);
    }


    @Test
    @DisplayName("현재까지 발급되지 않은 인증코드를 전송한다.")
    void sendUniquePin(){
        //given
        given(randomTextGenerator.generateSixNumbers()).willReturn("123456").willReturn("567890").willReturn("345678");
        given(certCodeRepository.doesExists("123456")).willReturn(true);
        given(certCodeRepository.doesExists("567890")).willReturn(true);
        given(certCodeRepository.doesExists("345678")).willReturn(false);

        //when
        certificationService.sendMobileCode("01012345678");

        //then
        verify(randomTextGenerator, times(3)).generateSixNumbers();
        verify(certCodeRepository, times(3)).doesExists(any());
    }


    @Test
    @DisplayName("인증 번호를 확인한다.")
    void verifyCode(){
        //given
        String pinNumbers = "123456";
        given(certCodeRepository.doesExists(pinNumbers)).willReturn(true);

        //when
        boolean result = certificationService.isValidNumber(pinNumbers);

        //then
        verify(certCodeRepository).doesExists(pinNumbers);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("전송된 인증번호가 없는 경우 false를 리턴한다.")
    void verifyCodeFailed(){
        //given
        String pinNumbers = "123456";
        given(certCodeRepository.doesExists(pinNumbers)).willReturn(false);

        //when
        boolean result = certificationService.isValidNumber(pinNumbers);

        //then
        verify(certCodeRepository).doesExists(pinNumbers);
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("올바른 핀번호인지 검증한다.")
    void doesValidPinNumbers(){
        //given
        String pinNumbers = "123456";
        given(certCodeRepository.doesExists(pinNumbers)).willReturn(true);

        //when
        boolean result = certificationService.isValidNumber(pinNumbers);

        //then
        verify(certCodeRepository).doesExists(pinNumbers);
        Assertions.assertThat(result).isTrue();
    }
}