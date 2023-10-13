package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class ReissueFailedException extends GlotException {
    private final ErrorCode errorCode;

    public ReissueFailedException() {
        super();
        errorCode = ErrorCode.REISSUE_FAILED;
    }
}
