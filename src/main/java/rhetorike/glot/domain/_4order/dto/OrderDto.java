package rhetorike.glot.domain._4order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._4order.vo.Payment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long planId;
    private int quantity;
    private Payment payment;
}
