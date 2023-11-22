package rhetorike.glot.domain._1auth.repository.blockedtoken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.global.config.redis.RedisConfig;
import rhetorike.glot.global.security.jwt.AccessToken;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(RedisConfig.class)
@DataRedisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Repository.class})
})
class BlockedTokenRedisRepositoryTest {
    @Autowired
    BlockedTokenRedisRepository blockedTokenRedisRepository;

    @Test
    @DisplayName("BlockedToken을 저장하고 조회한다.")
    void saveAndFind(){
        //given
        User user = Personal.builder().id(1L).build();
        AccessToken accessToken = AccessToken.generatedFrom(user);

        //when
        blockedTokenRedisRepository.save(accessToken);
        boolean result = blockedTokenRedisRepository.doesExist(accessToken.getContent());

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("BlockedToken을 저장하고 삭제한다.")
    void delete(){
        //given
        User user = Personal.builder().id(1L).build();
        AccessToken accessToken = AccessToken.generatedFrom(user);
        blockedTokenRedisRepository.save(accessToken);

        //when
        blockedTokenRedisRepository.delete(accessToken);
        boolean result = blockedTokenRedisRepository.doesExist(accessToken.getContent());

        //then
        assertThat(result).isFalse();
    }
}