package rhetorike.glot.domain._2user.reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);

    List<User> findByEmailAndName(String email, String name);

    List<User> findByMobileAndName(String mobile, String name);

    Optional<User> findByAccountIdAndEmailAndName(String accountId, String email, String name);

    List<User> findBySubscription(Subscription subscription);
    List<OrganizationMember> findMemberBySubscription(Subscription subscription);

}
