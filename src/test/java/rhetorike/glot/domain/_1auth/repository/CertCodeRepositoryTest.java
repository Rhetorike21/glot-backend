package rhetorike.glot.domain._1auth.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import rhetorike.glot.domain._1auth.entity.EmailCertCode;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.global.config.redis.RedisConfig;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(RedisConfig.class)
@DataRedisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Repository.class})
})
public class CertCodeRepositoryTest {

    @Autowired
    CertCodeRepository certCodeRepository;

    @Test
    @DisplayName("CertCode를 저장한다.")
    void saveAndFind() {
        //given
        certCodeRepository.save(new EmailCertCode("1234", "email"));

        //when
        boolean result = certCodeRepository.doesExists("1234");

        //then
        assertThat(result).isTrue();
    }



    @Test
    @DisplayName("CertCode를 삭제한다.")
    void delete() {
        //given
        certCodeRepository.save(new EmailCertCode("1234", "email"));

        //when
        certCodeRepository.delete("1234");
        boolean result = certCodeRepository.doesExists("1234");

        //then
        assertThat(result).isFalse();
    }
}
