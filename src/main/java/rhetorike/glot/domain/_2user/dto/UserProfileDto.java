package rhetorike.glot.domain._2user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.User;

public class UserProfileDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetRequest {
        private String userType;
        private String name;
        private String phone;
        private String mobile;
        private String email;
        private String accountId;
        private String subscription;
        private String orgName;

        public GetRequest(User user) {
            this.userType = user.getUserType();
            this.name = user.getName();
            this.phone = user.getPhone();
            this.mobile = user.getMobile();
            this.email = user.getEmail();
            this.accountId = user.getAccountId();
            if (user.getSubscription() != null) {
                this.subscription = user.getSubscription().getName();
            }
            if (user instanceof Organization organization) {
                this.orgName = organization.getOrganizationName();
            }
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateParam {
        private String name;
        private String mobile;
        private String email;
        private String password;
    }
}


