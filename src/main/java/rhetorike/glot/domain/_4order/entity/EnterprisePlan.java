package rhetorike.glot.domain._4order.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
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

    public EnterprisePlan(Long id, String name, long price, Period expiryPeriod) {
        super(id, name, price, expiryPeriod);
    }

    @Override
    public Subscription subscribe(User owner, int size) {
        if (owner instanceof Organization manager){
            ArrayList<User> members = new ArrayList<>();
            members.add(owner);
            members.addAll(manager.generateMembers(size));
            LocalDate endDate = LocalDate.now().plus(this.expiryPeriod);
            return Subscription.newSubscription(endDate, members);
        }
        throw new ClassCastException();
    }
}
