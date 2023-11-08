package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;

import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`order`")
public class Order extends BaseTimeEntity {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn
    private User user;
    @ManyToOne
    @JoinColumn
    private Plan plan;
    private int quantity;
    private long totalPrice;
    private long supplyPrice;
    private long vat;
    private OrderStatus status;

    public long totalAmount() {
        return plan.getPrice() * quantity;
    }

    public static Order newOrder(User user, Plan plan, int quantity) {
        long supplyPrice = Math.round(plan.getPrice() * quantity);
        long vat = Math.round(supplyPrice * 0.1);
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .plan(plan)
                .quantity(quantity)
                .totalPrice(supplyPrice + vat)
                .supplyPrice(supplyPrice)
                .vat(vat)
                .status(OrderStatus.READY)
                .build();
    }

    public void complete(){
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(){
        this.status = OrderStatus.CANCELLED;
    }

    public Subscription subscribe() {
        return plan.subscribe(user, quantity);
    }
}
