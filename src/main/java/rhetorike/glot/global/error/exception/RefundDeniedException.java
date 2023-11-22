package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class RefundDeniedException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public RefundDeniedException() {
        super();
        errorCode = ErrorCode.REFUND_DENIED;
    }
}
