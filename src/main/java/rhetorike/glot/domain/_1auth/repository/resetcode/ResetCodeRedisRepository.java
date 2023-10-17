package rhetorike.glot.domain._1auth.repository.resetcode;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.ResetCode;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResetCodeRedisRepository implements ResetCodeRepository{
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String PREFIX = "RESET:";
    @Override
    public ResetCode save(ResetCode resetCode) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = PREFIX + resetCode.getAccountId();
        operations.set(key, resetCode.getCode());
        redisTemplate.expire(key, Duration.ofMinutes(30));
        return resetCode;
    }

    @Override
    public Optional<ResetCode> findByAccountId(String accountId) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = PREFIX + accountId;
        String value = (String) operations.get(key);
        if (value == null){
            return Optional.empty();
        }
        return Optional.of(new ResetCode(accountId, value));
    }

    @Override
    public void deleteByAccountId(String accountId) {
        String key = PREFIX + accountId;
        redisTemplate.delete(key);
    }
}
