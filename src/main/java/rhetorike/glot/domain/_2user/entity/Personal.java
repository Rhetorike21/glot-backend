package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._1auth.dto.SignUpDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.util.Collections;
import java.util.List;

@DiscriminatorValue("personal")
@NoArgsConstructor
@Entity
public class Personal extends User {

    @Builder
    public Personal(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards) {
        super(id, accountId, password, name, phone, mobile, email, marketingAgreement, roles, writingBoards);
    }
}
