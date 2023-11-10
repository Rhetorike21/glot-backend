package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class CertificationFailedException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public CertificationFailedException() {
        super();
        errorCode = ErrorCode.CERTIFICATION_FAILED;
    }
}
