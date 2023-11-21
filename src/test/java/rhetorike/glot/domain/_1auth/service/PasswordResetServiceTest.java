package rhetorike.glot.domain._1auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class PasswordResetServiceTest {
    @InjectMocks
    PasswordResetService passwordResetService;
    @Mock
    UserRepository userRepository;
    @Mock
    CertificationService certificationService;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("비밀번호 재설정 메일을 전송한다. ")
    void sendResetLink(){
        //given
        String accountId = "hong1234";
        String name = "홍길동";
        String email = "hong@naver.com";
        PasswordResetDto.LinkRequest requestDto = new PasswordResetDto.LinkRequest(accountId, name, email);
        User user = Personal.builder().build();
        given(userRepository.findByAccountIdAndEmailAndName(accountId, email, name)).willReturn(Optional.of(user));

        //when
        passwordResetService.sendResetLinkByEmail(requestDto);

        //then
        verify(userRepository).findByAccountIdAndEmailAndName(accountId, email, name);
    }

    @Test
    @DisplayName("비밀번호를 재설정한다.")
    void resetPassword(){
        //given
        String accountId = "hong1234";
        String code = "123456";
        String password = "abcd1234";
        PasswordResetDto.Request requestDto = new PasswordResetDto.Request(accountId, code, password);
        User user = Personal.builder().build();
        given(userRepository.findByAccountId(accountId)).willReturn(Optional.of(user));
        given(passwordEncoder.encode(password)).willReturn("(encoded)" + password);

        //when
        passwordResetService.resetPassword(requestDto);

        //then
        verify(userRepository).findByAccountId(accountId);
        verify(passwordEncoder).encode(password);
    }
}