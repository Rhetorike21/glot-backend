package rhetorike.glot.global.security.jwt;

import io.jsonwebtoken.*;
import lombok.Getter;
import rhetorike.glot.global.error.exception.JwtExpiredException;
import rhetorike.glot.global.error.exception.JwtWrongFormatException;

import java.util.Objects;

@Getter
public abstract class ServiceToken {
    protected String content;

    public abstract Claims extractClaims();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ServiceToken that = (ServiceToken) object;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    public long getExpiration(){
        if (this instanceof AccessToken){
            return AccessToken.EXPIRATION_MILLI;
        }
        return RefreshToken.EXPIRATION_MILLI;
    }
}
