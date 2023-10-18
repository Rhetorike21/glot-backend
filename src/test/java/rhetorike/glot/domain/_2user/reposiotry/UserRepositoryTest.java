package rhetorike.glot.domain._2user.reposiotry;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.setup.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RepositoryTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("개인 사용자를 저장하고 조회한다.")
    void saveAndFindPersonal() {
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
    void saveAndFindOrganization() {
        //given
        User user = new Organization();
        User saved = userRepository.save(user);

        //when
        Optional<User> found = userRepository.findById(saved.getId());

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("이메일과 이름으로 사용자를 조회한다.")
    void findByEmailAndName() {
        //given
        User user1 = userRepository.save(Personal.builder().email("email1").name("name1").build());
        User user2 = userRepository.save(Personal.builder().email("email2").name("name2").build());
        User user3 = userRepository.save(Personal.builder().email("email1").name("name1").build());
        User user4 = userRepository.save(Personal.builder().email("email3").name("name1").build());

        //when
        List<User> found = userRepository.findByEmailAndName("email1", "name1");

        //then
        assertThat(found).containsExactlyInAnyOrder(user1, user3);
    }

    @Test
    @DisplayName("전화번호와 이름으로 사용자를 조회한다.")
    void findByMobileAndName() {
        //given
        User user1 = userRepository.save(Personal.builder().mobile("01011111111").name("name1").build());
        User user2 = userRepository.save(Personal.builder().mobile("01099999999").name("name2").build());
        User user3 = userRepository.save(Personal.builder().mobile("01011111111").name("name1").build());
        User user4 = userRepository.save(Personal.builder().mobile("01055555555").name("name1").build());

        //when
        List<User> found = userRepository.findByMobileAndName("01011111111", "name1");

        //then
        assertThat(found).containsExactlyInAnyOrder(user1, user3);
    }

    @Test
    @DisplayName("비밀번호를 변경한다.")
    void changePassword() {
        //given
        User user = Personal.builder().password("old").build();
        User saved = userRepository.save(user);
        saved.changePassword("new");

        //when
        Optional<User> found = userRepository.findById(saved.getId());

        //then
        assertThat(found).isNotEmpty();
        assertThat(found.get().getPassword()).isEqualTo("new");
    }
}