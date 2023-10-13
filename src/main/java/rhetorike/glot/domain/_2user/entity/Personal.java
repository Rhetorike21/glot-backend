package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@DiscriminatorValue("personal")
@NoArgsConstructor
@Entity
public class Personal extends User {

    @Builder
    public Personal(Long id, String username, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles){
        super(id, username, password, name, phone, mobile, email, marketingAgreement, roles);
    }
}
