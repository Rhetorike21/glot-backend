package rhetorike.glot.domain._2user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.dto.UserDto;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;
    public UserProfileDto.GetRequest getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new UserProfileDto.GetRequest(user);
    }

    public User findOrCreateMember(String accountId){
        return userRepository.findByAccountId(accountId)
                .orElseGet(() -> userRepository.save(OrganizationMember.newOrganizationMember(accountId, passwordEncoder.encode(accountId))));
    }

    @Transactional
    public void updateUserProfile(UserProfileDto.UpdateParam requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.update(requestDto, passwordEncoder);
    }

    @Transactional
    public void activate(Long managerId, UserDto.ActivateRequest requestDto) {
        User manager = userRepository.findById(managerId).orElseThrow(UserNotFoundException::new);
        validateOrganization(manager);
        Subscription subscription = subscriptionRepository.findByOrderer(manager).orElseThrow(ResourceNotFoundException::new);
        User member = userRepository.findByAccountId(requestDto.getAccountId()).orElseThrow(UserNotFoundException::new);
        if (subscription.equals(member.getSubscription())){
            member.updateActive(requestDto.isActive());
        }
    }

    private Organization validateOrganization(User manager) {
        if (manager instanceof Organization){
            return (Organization) manager;
        }
        throw new AccessDeniedException();
    }
}
