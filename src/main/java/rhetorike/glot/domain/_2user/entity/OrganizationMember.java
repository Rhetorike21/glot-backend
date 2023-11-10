package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.util.ArrayList;
import java.util.List;

@DiscriminatorValue("org_member")
@NoArgsConstructor
@Entity
public class OrganizationMember extends User{

    @Builder
    public OrganizationMember(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards, Subscription subscription){
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards, subscription);
    }

    public static OrganizationMember newOrganizationMember(String accountId, String password){
        return OrganizationMember.builder()
                .accountId(accountId)
                .password(password)
                .build();
    }

}
