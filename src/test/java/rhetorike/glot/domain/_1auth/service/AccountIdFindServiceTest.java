package rhetorike.glot.domain._1auth.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.service.smscert.SmsCertificationService;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.CertificationFailedException;
import rhetorike.glot.global.util.email.EmailService;
import rhetorike.glot.setup.ServiceTest;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class AccountIdFindServiceTest {

    @InjectMocks
    AccountIdFindService accountIdFindService;
    @Mock
    EmailService emailService;
    @Mock
    UserRepository userRepository;
    @Mock
    SmsCertificationService smsCertificationService;

    @Test
    @DisplayName("이메일로 아이디를 찾는다.")
    void findByEmail(){
        //given
        String name = "홍길동";
        String email = "hong@naver.com";
        String accountId = "hong1234";
        AccountIdFindDto.EmailRequest requestDto = new AccountIdFindDto.EmailRequest(name, email);
        given(userRepository.findByEmailAndName(email, name)).willReturn(List.of(Personal.builder().accountId(accountId).build()));

        //when
        accountIdFindService.findAccountIdByEmail(requestDto);

        //then
        verify(userRepository).findByEmailAndName(email, name);
    }

    @Test
    @DisplayName("휴대폰으로 아이디를 찾는다.")
    void findByMobile(){
        //given
        String name = "홍길동";
        String mobile = "01076078701";
        String code = "123456";
        AccountIdFindDto.MobileRequest requestDto = new AccountIdFindDto.MobileRequest(name, mobile, code);
        given(userRepository.findByMobileAndName(mobile, name)).willReturn(List.of(Personal.builder().accountId("hong1234").build()));
        given(smsCertificationService.doesValidPinNumbers(code)).willReturn(true);

        //when
        accountIdFindService.findAccountIdByMobile(requestDto);

        //then
        verify(userRepository).findByMobileAndName(mobile, name);
        verify(smsCertificationService).doesValidPinNumbers(code);
    }

    @Test
    @DisplayName("[휴대폰으로 아이디 찾기] 인증 코드가 유효하지 않는 경우 예외 발생")
    void findByMobileThrowCertificationFailed(){
        //given
        String name = "홍길동";
        String mobile = "01076078701";
        String code = "123456";
        AccountIdFindDto.MobileRequest requestDto = new AccountIdFindDto.MobileRequest(name, mobile, code);
        given(userRepository.findByMobileAndName(mobile, name)).willReturn(List.of(Personal.builder().accountId("hong1234").build()));
        given(smsCertificationService.doesValidPinNumbers(code)).willReturn(false);

        //when

        //then
        Assertions.assertThatThrownBy(() -> accountIdFindService.findAccountIdByMobile(requestDto)).isInstanceOf(CertificationFailedException.class);
    }
}