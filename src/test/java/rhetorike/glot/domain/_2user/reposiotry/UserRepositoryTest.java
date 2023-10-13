package rhetorike.glot.domain._2user.reposiotry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.setup.RepositoryTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("개인 사용자를 저장하고 조회한다.")
    void saveAndFindPersonal(){
        //given
        User user = new Personal();
        User saved = userRepository.save(user);

        //when
        Optional<User> found = userRepository.findById(saved.getId());

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("기관 사용자를 저장하고 조회한다.")
    void saveAndFindOrganization(){
        //given
        User user = new Organization();
        User saved = userRepository.save(user);

        //when
        Optional<User> found = userRepository.findById(saved.getId());

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(saved);
    }
}