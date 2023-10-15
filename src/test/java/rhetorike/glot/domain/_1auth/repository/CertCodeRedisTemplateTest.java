package rhetorike.glot.domain._1auth.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import rhetorike.glot.global.config.redis.RedisConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RedisConfig.class)
@DataRedisTest
class CertCodeRedisTemplateTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void beforeEach(){
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("CertCode를 저장하고 조회한다.")
    void saveAndFind(){
        //given
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String prefix = "CERT_CODE:";
        String key = prefix + "1234";

        //when
        operations.put(key, "checked", false);
        redisTemplate.expire(key, Duration.ofMinutes(10));

        //then
        assertThat(redisTemplate.hasKey(key)).isTrue();
        assertThat((Boolean) operations.get(key, "checked")).isFalse();
    }

    @Test
    @DisplayName("CertCode의 checked 속성을 갱신한다.")
    void update(){
        //given
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String prefix = "CERT_CODE:";
        String key = prefix + "1234";
        operations.put(key, "checked", false);
        redisTemplate.expire(key, Duration.ofMinutes(10));

        //when
        operations.put(key, "checked", true);

        //then
        assertThat(redisTemplate.hasKey(key)).isTrue();
        assertThat((Boolean) operations.get(key, "checked")).isTrue();
        assertThat(operations.size(key)).isEqualTo(1);
    }


    @Test
    @DisplayName("해당 CertCode를 제거한다.")
    void delete(){
        //given
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        String prefix = "CERT_CODE:";
        String key = prefix + "1234";
        operations.put(key, "checked", false);
        redisTemplate.expire(key, Duration.ofMinutes(10));

        //when
        redisTemplate.delete(key);

        //then
        assertThat(redisTemplate.hasKey(key)).isFalse();
    }
}