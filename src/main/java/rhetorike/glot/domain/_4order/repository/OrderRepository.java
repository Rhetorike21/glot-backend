package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserOrderByCreatedTimeDesc(User user);

    @Query(" select o from Order o join Subscription sc on o.subscription = sc where sc.continued = :continued and sc.endDate = :endDate")
    List<Order> findAllByContinuedAndEndDate(@Param("continued") boolean continued, @Param("endDate") LocalDate endDate);
}
