package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean continued;
    private LocalDate endDate;
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<User> members;

    @Builder
    public Subscription(Long id, boolean continued, LocalDate endDate, List<User> members){
        this.id = id;
        this.continued = continued;
        this.endDate = endDate;
        this.members = new ArrayList<>();
        this.members.addAll(members);
    }

    public static Subscription newSubscription(LocalDate localDate, List<User> members){
        return Subscription.builder()
                .continued(true)
                .endDate(localDate)
                .members(members)
                .build();
    }
}
