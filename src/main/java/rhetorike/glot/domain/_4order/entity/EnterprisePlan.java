package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@DiscriminatorValue("enterprise")
public class EnterprisePlan extends Plan{

    @Builder
    public EnterprisePlan(Long id, String name, long price, PlanPeriod planPeriod) {
        super(id, name, price, planPeriod);
    }
}
