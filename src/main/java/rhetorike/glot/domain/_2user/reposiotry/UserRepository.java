package rhetorike.glot.domain._2user.reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._2user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
