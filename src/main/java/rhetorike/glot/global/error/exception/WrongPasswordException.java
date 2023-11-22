package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class WrongPasswordException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public WrongPasswordException() {
        super();
        errorCode = ErrorCode.WRONG_PASSWORD;
    }
}
