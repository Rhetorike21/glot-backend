package rhetorike.glot.domain._1auth.repository.certcode;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.CertCode;

import java.time.Duration;

@Primary
@Repository
@RequiredArgsConstructor
public class CertCodeRepository {
    private final static String BLANK = "";
    private final static String PREFIX = "CERT_CODE:";
    private final static Duration EXPIRATION = Duration.ofMinutes(4);
    private final RedisTemplate<String, Object> redisTemplate;


    public void save(CertCode certCode) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = PREFIX + certCode.getNumber();
        operations.set(key, BLANK);
        redisTemplate.expire(key, EXPIRATION);
    }

    public boolean doesExists(String number) {
        String key = PREFIX + number;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void delete(String number) {
        String key = PREFIX + number;
        redisTemplate.delete(key);
    }
}
