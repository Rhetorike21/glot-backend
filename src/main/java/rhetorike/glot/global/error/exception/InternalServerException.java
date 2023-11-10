package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class InternalServerException extends GlotUncheckedException {
    private final Exception originalException;
    private final ErrorCode errorCode;

    public InternalServerException(Exception originalException) {
        super();
        this.originalException = originalException;
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
