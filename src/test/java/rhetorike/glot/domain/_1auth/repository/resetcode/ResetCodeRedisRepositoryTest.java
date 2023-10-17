package rhetorike.glot.domain._1auth.repository.resetcode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.ResetCode;
import rhetorike.glot.global.config.redis.RedisConfig;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RedisConfig.class)
@DataRedisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Repository.class})
})
class ResetCodeRedisRepositoryTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ResetCodeRedisRepository resetCodeRedisRepository;

    @AfterEach
    void afterEach(){
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }
    @Test
    @DisplayName("ResetCode를 저장하고 조회한다.")
    void saveAndFind(){
        //given
        String accountId = "hong@naver.com";
        ResetCode saved = resetCodeRedisRepository.save(ResetCode.randomResetCode(accountId));

        //when
        Optional<ResetCode> found = resetCodeRedisRepository.findByAccountId(accountId);

        //then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("ResetCode를 제거한다.")
    void delete(){
        //given
        String accountId = "hong@naver.com";
        resetCodeRedisRepository.save(ResetCode.randomResetCode(accountId));

        //when
        resetCodeRedisRepository.deleteByAccountId(accountId);
        Optional<ResetCode> found = resetCodeRedisRepository.findByAccountId(accountId);

        //then
        assertThat(found).isEmpty();
    }

}