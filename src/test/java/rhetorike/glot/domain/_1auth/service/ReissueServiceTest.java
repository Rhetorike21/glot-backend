package rhetorike.glot.domain._1auth.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.RefreshTokenExpiredException;
import rhetorike.glot.global.error.exception.ReissueFailedException;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@Slf4j
@ServiceTest
class ReissueServiceTest {

    @InjectMocks
    ReissueService reissueService;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("토큰을 재발급한다.")
    void reissue() {
        //given
        User user = Personal.builder().id(1L).build();

        AccessToken accessTokenExpired = AccessToken.generatedFrom(user);
        RefreshToken refreshToken = RefreshToken.generatedFrom(user);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //when
        reissueService.reissue(accessTokenExpired.getContent(), refreshToken.getContent());

        //then
        Mockito.verify(userRepository, times(3)).findById(any());
    }

    @Test
    @DisplayName("주어진 액세스 토큰과 리프레시 토큰이 일치하지 않는 경우 토큰 재발급에 실패한다.")
    void reissueFailed() {
        User user1 = Personal.builder().id(1L).build();
        User user2 = Personal.builder().id(2L).build();
        AccessToken accessTokenExpired = AccessToken.generatedFrom(user1);
        RefreshToken refreshToken = RefreshToken.generatedFrom(user2);
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));
        given(userRepository.findById(2L)).willReturn(Optional.of(user2));

        //when

        //then
        Assertions.assertThatThrownBy(() -> reissueService.reissue(accessTokenExpired.getContent(), refreshToken.getContent())).isInstanceOf(ReissueFailedException.class);
    }


    @Test
    @Disabled
    @DisplayName("리프레시 토큰이 만료된 경우 예외가 발생한다.")
    void reissueFailedLoginRequired() {
        //given
        User user = Personal.builder().id(1L).build();
        AccessToken accessTokenExpired = AccessToken.generatedFrom(user);
        RefreshToken refreshTokenExpired = RefreshToken.generatedFrom(user);
        given(refreshTokenExpired.extractClaims()).willThrow(RefreshTokenExpiredException.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //when

        //then
        Assertions.assertThatThrownBy(() -> reissueService.reissue(accessTokenExpired.getContent(), refreshTokenExpired.getContent()))
                .isInstanceOf(RefreshTokenExpiredException.class);
    }
}