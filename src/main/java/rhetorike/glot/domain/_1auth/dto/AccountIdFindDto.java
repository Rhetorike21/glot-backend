package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AccountIdFindDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest{
        private String name;
        private String email;
    }
}
