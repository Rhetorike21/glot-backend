package rhetorike.glot.domain._4order.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String cardNumber;
    private String expiry;
    private String birthDate;
    private String password;
}
