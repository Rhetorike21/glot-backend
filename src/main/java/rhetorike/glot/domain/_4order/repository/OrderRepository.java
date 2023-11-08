package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserOrderByCreatedTimeDesc(User user);
}
