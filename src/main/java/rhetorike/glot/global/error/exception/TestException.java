package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class TestException extends GlotCheckedException {
    private final ErrorCode errorCode;

    public TestException() {
        super();
        errorCode = ErrorCode.USER_EXIST;
    }
}
