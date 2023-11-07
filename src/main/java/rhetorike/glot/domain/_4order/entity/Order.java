package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`order`")
public class Order {
    @Id
    private String id;
    @OneToOne
    @JoinColumn
    private User user;
    @OneToOne
    @JoinColumn
    private Plan plan;
    private int number;

    public long totalAmount(){
        return plan.getPrice() * number;
    }

    public static Order newOrder(User user, Plan plan, int number) {
        return new Order(UUID.randomUUID().toString(), user, plan, number);
    }

    public Subscription subscribe(){
        return plan.subscribe(user, number);
    }
}
