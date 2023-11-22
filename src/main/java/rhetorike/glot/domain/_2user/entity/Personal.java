package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDateTime;
import java.util.List;

@DiscriminatorValue("personal")
@NoArgsConstructor
@Entity
public class Personal extends User {

    @Builder
    public Personal(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards, Subscription subscription, boolean active, LocalDateTime lastLoggedInAt, Language language) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards, subscription, active, lastLoggedInAt, language);
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
    public void update(UserProfileDto.UpdateParam requestDto, PasswordEncoder passwordEncoder) {
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
            this.password = passwordEncoder.encode(requestDto.getPassword());
        }
        if (requestDto.getLanguage() != null){
            this.language = Language.findByName(requestDto.getLanguage());
        }
    }
}
