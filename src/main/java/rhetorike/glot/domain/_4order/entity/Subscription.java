package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean continued;
    private LocalDate startDate;
    private LocalDate endDate;
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<User> members;
    @OneToOne(mappedBy = "subscription", fetch = FetchType.LAZY)
    private Order order;

    @Builder
    public Subscription(Long id, boolean continued, LocalDate startDate, LocalDate endDate, Order order) {
        this.id = id;
        this.continued = continued;
        this.startDate = startDate;
        this.endDate = endDate;
        this.order = order;
        this.members = new ArrayList<>();
    }

    public void setMembers(List<? extends User> members) {
        this.members = new ArrayList<>();
        this.members.addAll(members);
    }

    public static Subscription newSubscription(LocalDate startDate, LocalDate endDate) {
        return Subscription.builder()
                .startDate(startDate)
                .endDate(endDate)
                .continued(true)
                .build();
    }

    public void unsubscribe() {
        this.continued = false;
    }
}
