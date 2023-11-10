package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "type")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"type", "planPeriod"}))
public abstract class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private long price;
    @Enumerated
    PlanPeriod planPeriod;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Plan plan = (Plan) object;
        return Objects.equals(id, plan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public LocalDate endDateFrom(LocalDate time) {
        return time.plus(this.planPeriod.getPeriod()).minusDays(1);
    }
}
