package rhetorike.glot.domain._1auth.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.ReissueFailedException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;
import rhetorike.glot.global.security.jwt.ServiceToken;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {
    private final UserRepository userRepository;

    /**
     * 엑세스 토큰을 재발급합니다.
     * 리프레시 토큰도 만료된 경우, 예외가 발생합니다.
     *
     * @param accessTokenValue  액세스 토큰 값
     * @param refreshTokenValue 리프레시 토큰 값
     * @return 재발급한 액세스 토큰
     */
    public TokenDto.AccessResponse reissue(String accessTokenValue, String refreshTokenValue) {
        AccessToken accessToken = AccessToken.from(accessTokenValue);
        RefreshToken refreshToken = RefreshToken.from(refreshTokenValue);
        User user = getUser(accessToken);
        if (isReissueAvailable(accessToken, refreshToken)) {
            return new TokenDto.AccessResponse(AccessToken.generatedFrom(user));
        }
        throw new ReissueFailedException();
    }
    private boolean isReissueAvailable(AccessToken accessToken, RefreshToken refreshToken) {
        User user1 = getUser(accessToken);
        User user2 = getUser(refreshToken);
        return user1.equals(user2);
    }
    private User getUser(ServiceToken serviceToken) {
        Claims claims = serviceToken.extractClaims();
        return userRepository.findById(Long.parseLong(claims.getSubject())).orElseThrow(UserNotFoundException::new);
    }
}
