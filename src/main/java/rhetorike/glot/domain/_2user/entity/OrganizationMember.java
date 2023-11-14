package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDateTime;
import java.util.List;

import static rhetorike.glot.global.constant.Role.*;

@DiscriminatorValue("org_member")
@NoArgsConstructor
@Entity
public class OrganizationMember extends User {

    @Builder
    public OrganizationMember(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards, Subscription subscription, boolean active, LocalDateTime lastLoggedInAt) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards, subscription, active, lastLoggedInAt);
    }

    @Override
    public String getUserType() {
        return null;
    }

    @Override
    public String generateEnterpriseName() {
        return "";
    }

    @Override
    public void update(UserProfileDto.UpdateParam requestDto, PasswordEncoder passwordEncoder) {
        if (requestDto.getName() != null) {
            this.name = requestDto.getName();
        }
        if (requestDto.getPassword() != null) {
            this.password = passwordEncoder.encode(requestDto.getPassword());
        }
    }

    public static OrganizationMember newOrganizationMember(String accountId, String password) {
        return OrganizationMember.builder()
                .accountId(accountId)
                .password(password)
                .active(true)
                .roles(List.of(USER.value(), MEMBER.value()))
                .build();
    }

}
