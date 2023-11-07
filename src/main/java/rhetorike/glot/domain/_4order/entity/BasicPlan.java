package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@NoArgsConstructor
@DiscriminatorValue("basic")
public class BasicPlan extends Plan {

    public BasicPlan(Long id, String name, long price, Period expiryPeriod) {
        super(id, name, price, expiryPeriod);
    }

    @Override
    public Subscription subscribe(User owner, int size) {
        LocalDate endDate = LocalDate.now().plus(this.expiryPeriod);
        return Subscription.newSubscription(endDate, List.of(owner));
    }
}
