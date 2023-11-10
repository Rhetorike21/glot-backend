package rhetorike.glot.domain._4order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.global.error.exception.AccessDeniedException;

import java.time.Period;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PlanPeriod {
    MONTH("1m", Period.ofMonths(1)),
    YEAR("1y", Period.ofYears(1));

    public final String name;
    private final Period period;

    public static PlanPeriod findByName(String name){
        return Arrays.stream(values())
                .filter(payStatus -> payStatus.getName().equals(name))
                .findFirst().orElseThrow(AccessDeniedException::new);
    }
}
