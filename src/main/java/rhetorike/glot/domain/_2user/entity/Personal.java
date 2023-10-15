package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._1auth.dto.SignUpRequest;

import java.util.Collections;
import java.util.List;

@DiscriminatorValue("personal")
@NoArgsConstructor
@Entity
public class Personal extends User {

    @Builder
    public Personal(Long id, String username, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles) {
        super(id, username, password, name, phone, mobile, email, marketingAgreement, roles);
    }

    public static Personal from(SignUpRequest.PersonalDto requestDto) {
        return rhetorike.glot.domain._2user.entity.Personal.builder()
                .username(requestDto.getAccountId())
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .mobile(requestDto.getMobile())
                .email(requestDto.getEmail())
                .marketingAgreement(requestDto.isMarketingAgreement())
                .roles(Collections.singletonList(DEFAULT_ROLE))
                .build();
    }
}
