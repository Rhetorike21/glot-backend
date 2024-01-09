package rhetorike.glot.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.dto.LoginDto;
import rhetorike.glot.domain._1auth.service.AuthService;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public LoginDto.Response login(LoginDto.Request requestDto) {
        LoginDto.Response loginResult = authService.login(requestDto);
        User user = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        if(!hasAdminRole(user)) {
            throw new UserNotFoundException();
        }
        return loginResult;
    }

    private Boolean hasAdminRole(User user) {
        return user
                .getAuthorities()
                .stream()
                .anyMatch(
                        authority -> authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ADMIN")
                );
    }
}
