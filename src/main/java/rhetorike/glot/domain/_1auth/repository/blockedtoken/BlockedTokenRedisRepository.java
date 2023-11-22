package rhetorike.glot.domain._1auth.repository.blockedtoken;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import rhetorike.glot.global.security.jwt.ServiceToken;

import java.time.Duration;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class BlockedTokenRedisRepository implements BlockedTokenRepository {
    private final static String PREFIX = "BLOCKED:";
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void save(ServiceToken serviceToken) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = PREFIX + serviceToken.getContent();
        operations.set(key, "");
        redisTemplate.expire(key, Duration.ofMillis(serviceToken.getExpiration()));
    }

    @Override
    public boolean doesExist(String jwt) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = PREFIX + jwt;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void delete(ServiceToken serviceToken) {
        String key = PREFIX + serviceToken.getContent();
        redisTemplate.delete(key);
    }
}
