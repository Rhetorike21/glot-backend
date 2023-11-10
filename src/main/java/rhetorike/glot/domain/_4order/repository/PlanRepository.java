package rhetorike.glot.domain._4order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.EnterprisePlan;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<BasicPlan> findBasicByPlanPeriod(PlanPeriod planPeriod);
    Optional<EnterprisePlan> findEnterpriseByPlanPeriod(PlanPeriod planPeriod);
}