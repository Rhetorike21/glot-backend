package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.global.security.jwt.AccessToken;
import rhetorike.glot.global.security.jwt.RefreshToken;

public class TokenDto {

    @Getter
    public static class FullResponse{
        private final String accessToken;
        private final String refreshToken;
        public FullResponse(AccessToken accessToken, RefreshToken refreshToken){
            this.accessToken = accessToken.getContent();
            this.refreshToken = refreshToken.getContent();
        }

    }

    @Getter
    public static class AccessResponse{
        private final String accessToken;

        public AccessResponse(AccessToken accessToken){
            this.accessToken = accessToken.getContent();
        }
    }
}
