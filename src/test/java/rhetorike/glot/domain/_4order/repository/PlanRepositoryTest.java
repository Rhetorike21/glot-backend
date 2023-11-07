package rhetorike.glot.domain._4order.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.setup.RepositoryTest;

import java.time.Period;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Rollback(value = false)
class PlanRepositoryTest {
    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("요금제 저장 및 조회")
    void saveAndFind(){
        //given
        Plan plan = planRepository.save(new BasicPlan(null, "월 베이직 요금제", 1000L, Period.ofMonths(1)));

        //when
        Optional<Plan> found = planRepository.findById(plan.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(plan);
    }



}