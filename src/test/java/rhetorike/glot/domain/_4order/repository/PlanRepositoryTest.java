package rhetorike.glot.domain._4order.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.EnterprisePlan;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.setup.RepositoryTest;

import java.time.Period;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class PlanRepositoryTest {
    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("요금제 저장 및 조회")
    void saveAndFind(){
        //given
        Plan plan = planRepository.save(new BasicPlan(null, "월 베이직 요금제", 1000L, PlanPeriod.MONTH));

        //when
        Optional<Plan> found = planRepository.findById(plan.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(plan);
    }


    @Test
    @DisplayName("타입으로 요금제 조회")
    void test(){
        //given
        BasicPlan basicPlan = planRepository.save(new BasicPlan(null, null, 0, PlanPeriod.MONTH));
        EnterprisePlan enterprisePlan = planRepository.save(new EnterprisePlan(null, null, 0, PlanPeriod.MONTH));

        //when
        Optional<BasicPlan> foundBasicPlan = planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH);
        Optional<EnterprisePlan> foundEnterprisePlan = planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH);

        //then
        Assertions.assertThat(foundBasicPlan).isPresent();
        Assertions.assertThat(foundBasicPlan.get()).isEqualTo(basicPlan);
        Assertions.assertThat(foundEnterprisePlan).isPresent();
        Assertions.assertThat(foundEnterprisePlan.get()).isEqualTo(enterprisePlan);
    }





}