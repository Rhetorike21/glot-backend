package rhetorike.glot.domain._1auth.dto;

import jakarta.servlet.annotation.HandlesTypes;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class LoginDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Request{
        private String accountId;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean subscribed;
        private TokenDto.FullResponse token;
    }

}
