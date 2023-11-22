package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class UserExistException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public UserExistException() {
        super();
        errorCode = ErrorCode.USER_EXIST;
    }
}
