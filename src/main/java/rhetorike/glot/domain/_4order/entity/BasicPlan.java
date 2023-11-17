package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Period;

@Entity
@NoArgsConstructor
@DiscriminatorValue("basic")
public class BasicPlan extends Plan {
    @Builder
    public BasicPlan(Long id, String name, long price, long discountedPrice, PlanPeriod expiryPeriod) {
        super(id, name, price, discountedPrice, expiryPeriod);
    }


    @Override
    public int past(Period period) {
        return period.getDays();
    }
}
