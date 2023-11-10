package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class UserNotFoundException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public UserNotFoundException() {
        super();
        errorCode = ErrorCode.USER_NOT_FOUND;
    }
}
