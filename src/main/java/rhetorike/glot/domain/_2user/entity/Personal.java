package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.global.util.RandomTextGenerator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DiscriminatorValue("personal")
@NoArgsConstructor
@Entity
public class Personal extends User {

    @Builder
    public Personal(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards, Subscription subscription, boolean active, LocalDateTime lastLoggedInAt) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards, subscription, active, lastLoggedInAt);
    }

    @Override
    public String getUserType() {
      return "개인";
    }

    @Override
    public String generateEnterpriseName() {
        return RandomStringUtils.randomAlphabetic(4);
    }

    @Override
    public void update(UserProfileDto.UpdateRequest requestDto) {
        if (requestDto.getName() != null) {
            this.name = requestDto.getName();
        }
        if (requestDto.getMobile() != null) {
            this.mobile = requestDto.getMobile();
        }
        if (requestDto.getEmail() != null) {
            this.email = requestDto.getEmail();
        }
        if (requestDto.getPassword() != null) {
            this.password = requestDto.getPassword();
        }
    }
}
