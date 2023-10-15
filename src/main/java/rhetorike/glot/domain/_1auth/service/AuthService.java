package rhetorike.glot.domain._1auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._1auth.dto.TokenDto;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.CertCodeRepository;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.CertificationFailedException;
import rhetorike.glot.global.error.exception.LoginFailedException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.security.JwtProvider;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CertCodeRepository certCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 서비스에 회원가입합니다.
     *
     * @param requestDto 회원가입에 필요한 필드
     */
    public void signUp(SignUpDto.BasicDto requestDto) {
        validateCode(requestDto.getCode());
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = requestDto.toUser(encodedPassword);
        userRepository.save(user);
    }

    private void validateCode(String code) {
        Optional<CertCode> certCodeOptional = certCodeRepository.findByPinNumbers(code);
        if (certCodeOptional.isEmpty() || !certCodeOptional.get().isChecked()) {
            throw new CertificationFailedException();
        }
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
            String accessToken = jwtProvider.createAccessToken(user);
            String refreshToken = jwtProvider.createRefreshToken(user);
            return new TokenDto.FullResponse(accessToken, refreshToken);
        }
        throw new LoginFailedException();
    }
}
