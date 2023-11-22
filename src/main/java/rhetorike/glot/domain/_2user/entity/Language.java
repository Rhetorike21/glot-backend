package rhetorike.glot.domain._2user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.domain._4order.entity.OrderStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Language {
    KOREAN("kr"),
    ;

    private final String name;
    public static Language findByName(String name){
        return Arrays.stream(values())
                .filter(payStatus -> payStatus.getName().equals(name))
                .findFirst().orElse(KOREAN);
    }
}
