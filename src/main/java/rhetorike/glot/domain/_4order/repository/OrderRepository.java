package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._4order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
