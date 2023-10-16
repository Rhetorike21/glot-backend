package rhetorike.glot.domain._1auth.repository.certcode;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.CertCode;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class CertCodeRedisRepository implements CertCodeRepository {
    private final static String PREFIX = "CERT_CODE:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<CertCode> findByPinNumbers(String pinNumbers) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String key = PREFIX + pinNumbers;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            boolean checked = (boolean) Objects.requireNonNull(operations.get(key, "checked"));
            return Optional.of(new CertCode(pinNumbers, checked));
        }
        return Optional.empty();
    }

    @Override
    public CertCode save(CertCode certCode) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String key = PREFIX + certCode.getPinNumbers();
        operations.put(key, "checked", false);
        redisTemplate.expire(key, Duration.ofMinutes(10));
        return certCode;
    }

    @Override
    public void update(CertCode certCode) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String key = PREFIX + certCode.getPinNumbers();
        operations.put(key, "checked", certCode.isChecked());
    }

    @Override
    public void delete(CertCode certCode) {
        String key = PREFIX + certCode.getPinNumbers();
        redisTemplate.delete(key);
    }
}
