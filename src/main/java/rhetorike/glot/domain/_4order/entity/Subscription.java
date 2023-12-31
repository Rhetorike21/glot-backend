package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean continued;
    private LocalDate startDate;
    private LocalDate endDate;
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<User> members;
    @OneToOne
    @JoinColumn
    private Order order;

    @Builder
    public Subscription(Long id, boolean continued, LocalDate startDate, LocalDate endDate, Order order, String name, List<User> members) {
        this.id = id;
        this.continued = continued;
        this.startDate = startDate;
        this.endDate = endDate;
        this.order = order;
        this.name = name;
        this.members = new ArrayList<>();
        if (members != null){
            this.members.addAll(members);
        }
    }

    public void setMembers(List<? extends User> members) {
        this.members = new ArrayList<>();
        this.members.addAll(members);
    }

    public static Subscription newSubscription(Order order) {
        return Subscription.builder()
                .startDate(order.getCreatedTime().toLocalDate())
                .endDate(order.getPlan().endDateFrom(order.getCreatedTime().toLocalDate()))
                .name(order.getPlan().getName())
                .order(order)
                .continued(true)
                .build();
    }

    public void unsubscribe() {
        this.continued = false;
    }

    public int getRemainDays() {
        return Period.between(LocalDate.now(), endDate).getDays();
    }

    public void changeOrder(Order order){
        this.order = order;
    }
}
