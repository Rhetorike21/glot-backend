package rhetorike.glot.domain._2user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserProfileDto.GetRequest getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new UserProfileDto.GetRequest(user);
    }

    public User findOrCreateMember(String accountId){
        return userRepository.findByAccountId(accountId)
                .orElseGet(() -> userRepository.save(OrganizationMember.newOrganizationMember(accountId, passwordEncoder.encode(accountId))));
    }

    @Transactional
    public void updateUserProfile(UserProfileDto.UpdateRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        requestDto.encodePassword(passwordEncoder);
        user.update(requestDto);
    }
}
