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
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRedisRepository;
import rhetorike.glot.global.config.redis.RedisConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(RedisConfig.class)
@DataRedisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Repository.class})
})
public class CertCodeRedisRepositoryTest {

    @Autowired
    CertCodeRedisRepository certCodeRedisRepository;
    @Test
    @DisplayName("CertCode를 저장하고 조회한다.")
    void saveAndFind(){
        //given
        CertCode saved = certCodeRedisRepository.save(new CertCode("1234", false));

        //when
        Optional<CertCode> found = certCodeRedisRepository.findByPinNumbers(saved.getPinNumbers());

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("CertCode를 갱신한다.")
    void update(){
        //given
        CertCode saved = certCodeRedisRepository.save(new CertCode("1234", false));
        saved.setChecked();

        //when
        certCodeRedisRepository.update(saved);
        Optional<CertCode> updated = certCodeRedisRepository.findByPinNumbers(saved.getPinNumbers());

        //then
        assertThat(updated).isNotEmpty();
        assertThat(updated.get().isChecked()).isTrue();
    }

    @Test
    @DisplayName("CertCode를 삭제한다.")
    void delete(){
        //given
        CertCode saved = certCodeRedisRepository.save(new CertCode("1234", false));

        //when
        certCodeRedisRepository.delete(saved);
        Optional<CertCode> found = certCodeRedisRepository.findByPinNumbers(saved.getPinNumbers());

        //then
        assertThat(found).isEmpty();
    }
}
