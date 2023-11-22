package rhetorike.glot.domain._1auth.repository.resetcode;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import rhetorike.glot.global.config.redis.RedisConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(RedisConfig.class)
@DataRedisTest
class ResetCodeRedisTemplateTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void afterEach(){
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("동일한 key에 대해 새로운 value가 주어지면, 기존 값을 덮어쓴다.")
    void overwrite(){
        //given
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = "key";
        operations.set(key, "oldValue");

        //when
        operations.set(key, "newValue");
        String result = (String) operations.get(key);

        //then
        assertThat(result).isEqualTo("newValue");
    }


}