package rhetorike.glot.domain._2user.reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._2user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);

    List<User> findByEmail(String email);
}
