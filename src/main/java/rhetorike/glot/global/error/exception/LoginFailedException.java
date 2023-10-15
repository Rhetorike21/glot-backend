package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class LoginFailedException extends GlotException {
    private final ErrorCode errorCode;

    public LoginFailedException() {
        super();
        errorCode = ErrorCode.LOGIN_FAILED;
    }
}
