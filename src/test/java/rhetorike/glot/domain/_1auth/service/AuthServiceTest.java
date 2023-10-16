package rhetorike.glot.domain._1auth.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.blockedtoken.BlockedTokenRepository;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.LoginFailedException;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
@ServiceTest
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    UserRepository userRepository;
    @Mock
    CertCodeRepository certCodeRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    BlockedTokenRepository blockedTokenRepository;

    @Test
    @DisplayName("개인 사용자로 서비스에 회원가입한다.")
    void signUpWithPersonal() {
        //given
        String rawPassword = "abc1234";
        String encodedPassword = "(encoded)abc1234";
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest("testpersonal", rawPassword, "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234");
        given(certCodeRepository.findByPinNumbers("1234")).willReturn(Optional.of(new CertCode("1234", true)));
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

        //when
        authService.signUp(requestDto);

        //then
    }

    @Test
    @DisplayName("기관 사용자로 서비스에 회원가입한다.")
    void signUpWithOrganization() {
        //given
        given(certCodeRepository.findByPinNumbers("1234")).willReturn(Optional.of(new CertCode("1234", true)));
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234", "한국고등학교");

        //when
        authService.signUp(requestDto);

        //then
    }

    @Test
    @DisplayName("서비스에 로그인한다.")
    void login() {
        //given
        String id = "abcd1234";
        String password = "efgh5678";
        String encodedPassword = "(encoded)efgh5678";
        User user = Personal.builder()
                .id(1L)
                .password(encodedPassword)
                .build();
        LoginDto requestDto = new LoginDto(id, password);
        given(userRepository.findByAccountId(id)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

        //when
        authService.login(requestDto);

        //then
        verify(userRepository).findByAccountId(id);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다.")
    void loginFailed() {
        //given
        String id = "abcd1234";
        String password = "efgh5678";
        String encodedPassword = "(encoded)password";
        User user = Personal.builder()
                .id(1L)
                .password(encodedPassword)
                .build();
        LoginDto requestDto = new LoginDto(id, password);
        given(userRepository.findByAccountId(id)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        //when
        Assertions.assertThatThrownBy(() -> authService.login(requestDto)).isInstanceOf(LoginFailedException.class);

        //then
        verify(userRepository).findByAccountId(id);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("서비스에서 로그아웃한다.")
    void logout() {
        //given
        User user = Personal.builder().id(1L).build();
        String accessToken = AccessToken.generatedFrom(user).getContent();
        String refreshToken = RefreshToken.generatedFrom(user).getContent();

        //when
        authService.logout(accessToken, refreshToken);

        //then
    }

    @Test
    @DisplayName("서비스에서 회원탈퇴한다.")
    void withdraw() {
        //given
        User user = Personal.builder().id(1L).build();
        String accessToken = AccessToken.generatedFrom(user).getContent();
        String refreshToken = RefreshToken.generatedFrom(user).getContent();

        //when
        authService.withdraw(accessToken, refreshToken);

        //then
    }
}