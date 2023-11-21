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
import rhetorike.glot.domain._1auth.repository.blockedtoken.BlockedTokenRedisRepository;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.service.SubscriptionService;
import rhetorike.glot.global.error.exception.SubscriptionRequiredException;
import rhetorike.glot.global.error.exception.WrongPasswordException;
import rhetorike.glot.global.error.exception.UserExistException;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    SubscriptionService subscriptionService;
    @Mock
    BlockedTokenRedisRepository blockedTokenRedisRepository;

    @Test
    @DisplayName("개인 사용자로 서비스에 회원가입한다.")
    void signUpWithPersonal() {
        //given
        String accountId = "testpersonal";
        String rawPassword = "abc1234";
        String encodedPassword = "(encoded)abc1234";
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest(accountId, rawPassword, "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234");
        given(certCodeRepository.doesExists("1234")).willReturn(true);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.findByAccountId(accountId)).willReturn(Optional.empty());

        //when
        authService.signUp(requestDto);

        //then
        verify(certCodeRepository).doesExists("1234");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("기관 사용자로 서비스에 회원가입한다.")
    void signUpWithOrganization() {
        //given
        String accountId = "testorganization";
        String rawPassword = "abc1234";
        String encodedPassword = "(encoded)abc1234";
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest(accountId, rawPassword, "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234", "한국고등학교");
        given(certCodeRepository.doesExists("1234")).willReturn(true);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.findByAccountId(accountId)).willReturn(Optional.empty());

        //when
        authService.signUp(requestDto);

        //then
        verify(certCodeRepository).doesExists("1234");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("회원가입 시, 아이디가 중복인 경우 예외가 발생한다.")
    void signUpFailed() {
        //given
        String accountId = "testorganization";
        String rawPassword = "abc1234";
        SignUpDto.OrgRequest requestDto = new SignUpDto.OrgRequest(accountId, rawPassword, "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234", "한국고등학교");
        given(userRepository.findByAccountId(accountId)).willReturn(Optional.of(Personal.builder().build()));

        //when

        //then
        Assertions.assertThatThrownBy(() -> authService.signUp(requestDto))
                .isInstanceOf(UserExistException.class);
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
        LoginDto.Request requestDto = new LoginDto.Request(id, password);
        given(userRepository.findByAccountId(id)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(subscriptionService.getSubStatus(user)).willReturn(SubscriptionService.SubStatus.FREE);

        //when
        authService.login(requestDto);

        //then
        verify(userRepository).findByAccountId(id);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(subscriptionService).getSubStatus(user);
    }

    @Test
    @DisplayName("[로그인] - 로그인에 성공하면, 최종 로그인 시각이 변경된다.")
    void updateLoggedInAt() {
        //given
        String id = "abcd1234";
        String password = "efgh5678";
        String encodedPassword = "(encoded)efgh5678";
        User user = Personal.builder()
                .id(1L)
                .password(encodedPassword)
                .build();
        LoginDto.Request requestDto = new LoginDto.Request(id, password);
        given(userRepository.findByAccountId(id)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(subscriptionService.getSubStatus(user)).willReturn(SubscriptionService.SubStatus.FREE);

        //when
        authService.login(requestDto);

        //then
        assertThat(user.getLastLoggedInAt()).isBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
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
        LoginDto.Request requestDto = new LoginDto.Request(id, password);
        given(userRepository.findByAccountId(id)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        //when
        Assertions.assertThatThrownBy(() -> authService.login(requestDto)).isInstanceOf(WrongPasswordException.class);

        //then
        verify(userRepository).findByAccountId(id);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("구독이 만료된 경우, 채번 계정은 로그인에 실패한다.")
    void loginFailedOrganizationMember() {
        //given
        String accountId = "abcd1234";
        String password = "efgh5678";
        String encodedPassword = "(encoded)password";
        User user = OrganizationMember.builder()
                .id(1L)
                .accountId(accountId)
                .password(encodedPassword)
                .build();
        LoginDto.Request requestDto = new LoginDto.Request(accountId, password);
        given(userRepository.findByAccountId(accountId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

        //when
        Assertions.assertThatThrownBy(() -> authService.login(requestDto)).isInstanceOf(SubscriptionRequiredException.class);

        //then
        verify(userRepository).findByAccountId(accountId);
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