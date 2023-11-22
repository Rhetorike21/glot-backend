package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AccountIdFindDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest{
        private String name;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobileRequest{
        private String name;
        private String mobile;
        private String code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobileResponse{
        private List<String> accountIds;
    }
}
