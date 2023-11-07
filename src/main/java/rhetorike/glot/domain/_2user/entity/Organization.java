package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@DiscriminatorValue("organization")
@NoArgsConstructor
@Entity
public class Organization extends User {
    @Column(length = 50)
    private String organizationName;

    @Builder
    public Organization(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, String organizationName, List<WritingBoard> writingBoards) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards);
        this.organizationName = organizationName;
    }

    public List<User> generateMembers(int size) {
        ArrayList<User> members = new ArrayList<>();
        for(int i=1; i<=size; i++){
            members.add(OrganizationMember.newOrganizationMember(makeMemberAccount(i)));
        }
        return members;
    }
    private String makeMemberAccount(int number) {
        return this.organizationName + String.format("%05d", number);
    }
}
