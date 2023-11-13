package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("basic")
public class BasicPlan extends Plan {

    public BasicPlan(Long id, String name, long price, PlanPeriod expiryPeriod) {
        super(id, name, price, expiryPeriod);
    }


}
