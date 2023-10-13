package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class ConnectionFailedException extends GlotException {
    private final ErrorCode errorCode;

    public ConnectionFailedException() {
        super();
        errorCode = ErrorCode.CONNECTION_FAILED;
    }
}
