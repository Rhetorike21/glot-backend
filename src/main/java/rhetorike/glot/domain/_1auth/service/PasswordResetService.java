package rhetorike.glot.domain._1auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.PasswordResetDto;
import rhetorike.glot.domain._1auth.entity.ResetCode;
import rhetorike.glot.domain._1auth.repository.resetcode.ResetCodeRepository;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.email.Email;
import rhetorike.glot.global.util.email.EmailService;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일로 비밀번호 재설정 링크를 전송합니다.
     *
     * @param requestDto 아이디, 이메일, 이름
     */
    public void sendResetLink(PasswordResetDto.EmailRequest requestDto) {
        User user = userRepository.findByAccountIdAndEmailAndName(requestDto.getAccountId(), requestDto.getEmail(), requestDto.getName()).orElseThrow(UserNotFoundException::new);
        ResetCode resetCode = ResetCode.randomResetCode(user.getAccountId());
        resetCodeRepository.save(resetCode);
        emailService.sendMail(Email.newPasswordResetEmail(user.getEmail(), resetCode));
    }

    /**
     * 비밀번호를 재설정합니다.
     *
     * @param requestDto 아이디, 인증코드, 새 비밀번호
     */
    @Transactional
    public void resetPassword(PasswordResetDto.ResetRequest requestDto) {
        User user = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        ResetCode saved = resetCodeRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(IllegalArgumentException::new);
        ResetCode given = ResetCode.from(requestDto.getAccountId(), requestDto.getCode());
        if (saved.equals(given)) {
            resetCodeRepository.deleteByAccountId(requestDto.getAccountId());
            user.changePassword(passwordEncoder.encode(requestDto.getPassword()));
            return;
        }
        throw new IllegalArgumentException();
    }
}
