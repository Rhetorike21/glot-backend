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
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToOne
    @JoinColumn
    private Subscription subscription;

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

    public static Order newReorder(Order order){
        long supplyPrice = order.getSupplyPrice();
        long vat = order.getVat();
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .user(order.getUser())
                .plan(order.getPlan())
                .quantity(order.getQuantity())
                .supplyPrice(supplyPrice)
                .vat(vat)
                .totalPrice(supplyPrice + vat)
                .status(OrderStatus.READY)
                .build();
    }

    public void setStatus(OrderStatus orderStatus){
        this.status = orderStatus;
    }

    public void cancel(){
        this.status = OrderStatus.CANCELLED;
    }


    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
