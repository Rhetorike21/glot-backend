package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@DiscriminatorValue("organization")
@NoArgsConstructor
@Entity
public class Organization extends User {
    @Column(length = 50)
    private String organizationName;

    @Builder
    public Organization(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, String organizationName, List<WritingBoard> writingBoards, Subscription subscription) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards, subscription);
        this.organizationName = organizationName;
    }
}
