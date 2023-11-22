package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.InvalidRequestException;

import java.time.LocalDate;
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
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Subscription subscription;
    private LocalDate firstOrderedDate;

    public static Order newOrder(User user, Plan plan, int quantity) {
        long supplyPrice = Math.round(plan.getDiscountedPrice() * quantity);
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
                .firstOrderedDate(LocalDate.now())
                .build();
    }

    public static Order newReorder(Order order, long numOfInvalid){
        long quantity = order.getQuantity() - numOfInvalid;
        if (quantity <= 0){
            throw new InvalidRequestException("주문 수량은 1보다 커야 합니다.");
        }
        long supplyPrice = Math.round(order.getPlan().getDiscountedPrice() * quantity);
        long vat = Math.round(supplyPrice * 0.1);
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .user(order.getUser())
                .plan(order.getPlan())
                .quantity(order.getQuantity())
                .supplyPrice(supplyPrice)
                .vat(vat)
                .totalPrice(supplyPrice + vat)
                .status(OrderStatus.READY)
                .firstOrderedDate(order.getFirstOrderedDate())
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

    public long calcTotalPriceWithoutDiscount(){
        long supplyPrice = Math.round(plan.getPrice() * quantity);
        long vat = Math.round(supplyPrice * 0.1);
        return supplyPrice + vat;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", supplyPrice=" + supplyPrice +
                ", vat=" + vat +
                ", status=" + status +
                ", firstOrderedDate=" + firstOrderedDate +
                '}';
    }
}
