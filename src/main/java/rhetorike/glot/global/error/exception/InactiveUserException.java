package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class InactiveUserException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public InactiveUserException() {
        super();
        errorCode = ErrorCode.INACTIVE_USER;
    }
}
