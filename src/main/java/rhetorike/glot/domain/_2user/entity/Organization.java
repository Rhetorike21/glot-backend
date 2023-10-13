package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@DiscriminatorValue("organization")
@NoArgsConstructor
@Entity
public class Organization extends User{
    @Column(length = 50)
    private String organizationName;

    @Builder
    public Organization(Long id, String username, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, String organizationName){
        super(id, username, password, name, phone, mobile, email, marketingAgreement, roles);
        this.organizationName = organizationName;
    }
}
