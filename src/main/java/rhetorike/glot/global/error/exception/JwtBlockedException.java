package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class JwtBlockedException extends GlotException {
    private final ErrorCode errorCode;

    public JwtBlockedException() {
        super();
        errorCode = ErrorCode.JWT_BLOCKED;
    }
}
