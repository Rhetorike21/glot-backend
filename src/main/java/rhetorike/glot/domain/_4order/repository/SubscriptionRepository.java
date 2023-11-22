package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    void deleteAllByEndDate(LocalDate localDate);

    List<Subscription> findAllByEndDate(LocalDate localDate);

    @Query(" select s from User u join Subscription s on u.subscription = s join Order o on s.order = o where o.user = :orderer ")
    Optional<Subscription> findByOrderer(@Param("orderer") User orderer);

    List<Subscription> findByContinuedIsTrueAndEndDate(LocalDate endDate);
}
