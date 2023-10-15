package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TokenDto {

    @Getter
    @AllArgsConstructor
    public static class FullResponse{
        private final String accessToken;
        private final String refreshToken;
    }
}
