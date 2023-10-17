package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PasswordResetDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest{
        private String accountId;
        private String name;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetRequest {
        private String accountId;
        private String code;
        private String password;
    }
}
