package rhetorike.glot.domain._4order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rhetorike.glot.global.error.exception.AccessDeniedException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    COMPLETED( "paid", "주문 완료"),
    CANCELLED( "cancelled", "주문 취소"),
    FAILED( "failed", "결제 실패"),
    READY("ready", "미결제");

    private final String name;
    private final String description;

    public static OrderStatus findByName(String name){
        return Arrays.stream(values())
                .filter(payStatus -> payStatus.getName().equals(name))
                .findFirst().orElseThrow(AccessDeniedException::new);
    }
}
