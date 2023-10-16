package rhetorike.glot.domain._1auth.repository.blockedtoken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import rhetorike.glot.global.config.redis.RedisConfig;

@Import(RedisConfig.class)
@DataRedisTest
public class BlockedTokenRedisTemplateTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void beforeEach(){
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("jwt를 저장한다.")
    void saveValue(){
        //given
        String key = "key";

        //when


        //then


    }



}
