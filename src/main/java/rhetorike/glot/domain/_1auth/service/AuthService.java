package rhetorike.glot.domain._1auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.SignUpRequest;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.CertCodeRepository;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.CertificationFailedException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CertCodeRepository certCodeRepository;

    /**
     * 서비스에 회원가입합니다.
     *
     * @param requestDto 회원가입에 필요한 필드
     */
    public void signUp(SignUpRequest.BasicDto requestDto) {
        validateCode(requestDto.getCode());
        User user = requestDto.toUser();
        userRepository.save(user);
    }

    private void validateCode(String code) {
        Optional<CertCode> certCodeOptional = certCodeRepository.findByPinNumbers(code);
        if (certCodeOptional.isEmpty() || !certCodeOptional.get().isChecked()) {
            throw new CertificationFailedException();
        }
    }
}
