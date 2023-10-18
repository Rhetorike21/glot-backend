package rhetorike.glot.domain._1auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.entity.BlockedToken;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.blockedtoken.BlockedTokenRepository;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.CertificationFailedException;
import rhetorike.glot.global.error.exception.LoginFailedException;
import rhetorike.glot.global.error.exception.UserExistException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CertCodeRepository certCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlockedTokenRepository blockedTokenRepository;

    /**
     * 서비스에 회원가입합니다.
     *
     * @param requestDto 회원가입에 필요한 필드
     */
    @Transactional
    public void signUp(SignUpDto.BasicDto requestDto) {
        if (userRepository.findByAccountId(requestDto.getAccountId()).isPresent()) {
            throw new UserExistException();
        }
        validateCode(requestDto.getCode());
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = requestDto.toUser(encodedPassword);
        userRepository.save(user);
    }
    private void validateCode(String code) {
        Optional<CertCode> certCodeOptional = certCodeRepository.findByPinNumbers(code);
        if (certCodeOptional.isPresent() && certCodeOptional.get().isChecked()) {
            certCodeRepository.delete(certCodeOptional.get());
            return;
        }
        throw new CertificationFailedException();
    }

    /**
     * 서비스에 로그인합니다.
     *
     * @param requestDto 아이디, 비밀번호
     * @return 액세스 토큰, 리프레시 토큰
     */
    public TokenDto.FullResponse login(LoginDto requestDto) {
        User user = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            AccessToken accessToken = AccessToken.generatedFrom(user);
            RefreshToken refreshToken = RefreshToken.generatedFrom(user);
            return new TokenDto.FullResponse(accessToken, refreshToken);
        }
        throw new LoginFailedException();
    }


    /**
     * 서비스에서 로그아웃합니다.
     *
     * @param accessValue  액세스 토큰
     * @param refreshValue 리프레시 토큰
     */
    public void logout(String accessValue, String refreshValue) {
        blockTokens(AccessToken.from(accessValue), RefreshToken.from(refreshValue));
    }

    /**
     * 서비스에서 회원탈퇴합니다.
     *
     * @param accessValue  액세스 토큰
     * @param refreshValue 리프레시 토큰
     */
    @Transactional
    public void withdraw(String accessValue, String refreshValue) {
        AccessToken accessToken = AccessToken.from(accessValue);
        RefreshToken refreshToken = RefreshToken.from(refreshValue);
        blockTokens(accessToken, refreshToken);

        Long userId = Long.parseLong(accessToken.extractClaims().getSubject());
        userRepository.deleteById(userId);
    }

    private void blockTokens(AccessToken accessToken, RefreshToken refreshToken) {
        blockedTokenRepository.save(accessToken);
        blockedTokenRepository.save(refreshToken);
    }
}
