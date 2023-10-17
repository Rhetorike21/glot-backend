package rhetorike.glot.domain._1auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.email.Email;
import rhetorike.glot.global.util.email.EmailService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountIdFindService {
    private final EmailService emailService;
    private final UserRepository userRepository;

    /**
     * 메일로 사용자의 아이디를 전송합니다.
     *
     * @param requestDto 이름, 이메일
     */
    public void sendAccountId(AccountIdFindDto.EmailRequest requestDto) {
        List<User> users = userRepository.findByEmailAndName(requestDto.getEmail(), requestDto.getName());
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }
        List<String> accountIds = users.stream()
                .map(User::getAccountId)
                .toList();
        Email email = Email.newAccountIdEmail(requestDto.getEmail(), accountIds);
        emailService.sendMail(email);
    }
}
