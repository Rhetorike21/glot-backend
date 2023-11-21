package rhetorike.glot.domain._4order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.global.error.exception.AccessDeniedException;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PlanPeriod {
    DAY("perDay", "매일",Period.ofDays(1), 1),
    MONTH("perMonth", "매월",Period.ofMonths(1), 31),
    YEAR("perYear","매년", Period.ofYears(1), 365);

    public final String name;
    public final String description;
    private final Period period;
    private final int unit;

    public static PlanPeriod findByName(String name){
        return Arrays.stream(values())
                .filter(payStatus -> payStatus.getName().equals(name))
                .findFirst().orElseThrow(AccessDeniedException::new);
    }
}
