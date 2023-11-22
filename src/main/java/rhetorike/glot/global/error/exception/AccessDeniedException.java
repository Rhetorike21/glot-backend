package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class AccessDeniedException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public AccessDeniedException() {
        super();
        errorCode = ErrorCode.ACCESS_DENIED;
    }
}
