package rhetorike.glot.domain._1auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ResetCode {
    private final String accountId;
    private final String code;

    private ResetCode(String accountId) {
        this.accountId = accountId;
        this.code = UUID.randomUUID().toString();
    }

    public static ResetCode randomResetCode(String accountId) {
        return new ResetCode(accountId, UUID.randomUUID().toString());
    }

    public static ResetCode from(String accountId, String code){
        return new ResetCode(accountId, code);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ResetCode resetCode = (ResetCode) object;
        return Objects.equals(accountId, resetCode.accountId) && Objects.equals(code, resetCode.code);
    }
    @Override
    public int hashCode() {
        return Objects.hash(accountId, code);
    }
}
