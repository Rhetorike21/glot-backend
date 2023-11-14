package rhetorike.glot.domain._2user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivateRequest{
        private String accountId;
        private boolean active;
    }
}
