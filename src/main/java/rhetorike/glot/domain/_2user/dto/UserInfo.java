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
    private final String id;

    public UserInfo(User user) {
        this.userType = user.getUsername();
        this.name = user.getUsername();
        this.phone = user.getUsername();
        this.mobile = user.getUsername();
        this.email = user.getUsername();
        this.id = user.getUsername();
    }
}
