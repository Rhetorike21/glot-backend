package rhetorike.glot.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.global.error.exception.JwtWrongFormatException;
import rhetorike.glot.global.error.exception.RefreshTokenExpiredException;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class RefreshToken extends ServiceToken {
    public static final Long EXPIRATION_MILLI = 31536000000L; // 1000 * 60 * 60 * 24 * 365 (1년)
    private final static byte[] SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
    private final String content;

    public static RefreshToken generatedFrom(User user) {
        Claims claims = injectValues(user);
        String content = Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
        return new RefreshToken(content);
    }

    public static RefreshToken from(String content) {
        return new RefreshToken(content);
    }

    private static Claims injectValues(User user) {
        long now = (new Date()).getTime();
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(user.getId()));
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(now + EXPIRATION_MILLI));
        claims.setId(UUID.randomUUID().toString());
        claims.put("authorities", authorities);
        return claims;
    }

    @Override
    public Claims extractClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(content)
                    .getBody();
        } catch (SecurityException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new JwtWrongFormatException();
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        }
    }
}
