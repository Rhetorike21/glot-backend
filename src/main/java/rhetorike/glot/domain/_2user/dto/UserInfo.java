package rhetorike.glot.domain._2user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.domain._2user.entity.User;

@Getter
@AllArgsConstructor
public class UserInfo {
    private final String userType;
    private final String name;
    private final String phone;
    private final String mobile;
    private final String email;
    private final String accountId;

    public UserInfo(User user) {
        this.userType = user.getType();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.mobile = user.getMobile();
        this.email = user.getEmail();
        this.accountId = user.getAccountId();
    }
}
