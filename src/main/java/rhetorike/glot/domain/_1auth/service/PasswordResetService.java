package rhetorike.glot.domain._1auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificationService certificationService;

    /**
     * 이메일로 비밀번호 재설정을 위한 인증코드를 전송합니다.
     *
     * @param requestDto 아이디, 이메일, 이름
     */
    public void sendResetLinkByEmail(PasswordResetDto.LinkRequest requestDto) {
        User user = userRepository.findByAccountIdAndEmailAndName(requestDto.getAccountId(), requestDto.getEmail(), requestDto.getName()).orElseThrow(UserNotFoundException::new);
        certificationService.sendEmailCode(user.getEmail());
    }

    /**
     * 비밀번호를 재설정합니다.
     *
     * @param requestDto 아이디, 인증코드, 새 비밀번호
     */
    @Transactional
    public void resetPassword(PasswordResetDto.Request requestDto) {
        User user = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        certificationService.deleteCodeIfValid(requestDto.getCode());
        user.changePassword(passwordEncoder.encode(requestDto.getPassword()));
    }
}
