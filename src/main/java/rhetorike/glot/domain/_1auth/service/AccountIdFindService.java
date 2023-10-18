package rhetorike.glot.domain._1auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._1auth.service.smscert.SmsCertificationService;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.CertificationFailedException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.email.Email;
import rhetorike.glot.global.util.email.EmailService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountIdFindService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SmsCertificationService smsCertificationService;

    /**
     * 메일로 사용자의 아이디를 전송합니다.
     *
     * @param requestDto 이름, 이메일
     */
    public void findAccountIdByEmail(AccountIdFindDto.EmailRequest requestDto) {
        List<User> users = userRepository.findByEmailAndName(requestDto.getEmail(), requestDto.getName());
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }
        List<String> accountIds = getAllAccountId(users);
        Email email = Email.newAccountIdEmail(requestDto.getEmail(), accountIds);
        emailService.sendMail(email);
    }


    /**
     * 휴대폰으로 사용자의 아이디를 찾습니다.
     *
     * @param requestDto 전화번호, 이름, 인증코드
     * @return 사용자의 아이디 목록
     */
    public AccountIdFindDto.MobileResponse findAccountIdByMobile(AccountIdFindDto.MobileRequest requestDto) {
        List<User> users = findUserByMobileAndName(requestDto);
        if (smsCertificationService.doesValidPinNumbers(requestDto.getCode())) {
            List<String> accountIds = getAllAccountId(users);
            return new AccountIdFindDto.MobileResponse(accountIds);
        }
        throw new CertificationFailedException();
    }

    @NotNull
    private List<User> findUserByMobileAndName(AccountIdFindDto.MobileRequest requestDto) {
        log.info(requestDto.getMobile());
        log.info(requestDto.getName());
        List<User> users = userRepository.findByMobileAndName(requestDto.getMobile(), requestDto.getName());
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }
        return users;
    }

    @NotNull
    private static List<String> getAllAccountId(List<User> users) {
        return users.stream()
                .map(User::getAccountId)
                .toList();
    }
}
