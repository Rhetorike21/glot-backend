package rhetorike.glot.domain._1auth.dto;

import jakarta.servlet.annotation.HandlesTypes;
import lombok.*;
import rhetorike.glot.domain._4order.service.SubscriptionService;

import static rhetorike.glot.domain._4order.service.SubscriptionService.*;


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
    @Builder
    public static class Response{
        private SubStatus subStatus;
        private TokenDto.FullResponse token;
    }

}
