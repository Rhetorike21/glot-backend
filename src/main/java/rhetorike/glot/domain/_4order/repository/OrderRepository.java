package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserOrderByCreatedTimeDesc(User user);

    Optional<Order> findTop1ByStatusAndUserOrderByCreatedTimeDesc(OrderStatus status, User user);
}

