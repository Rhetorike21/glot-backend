package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class ResourceNotFoundException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException() {
        super();
        errorCode = ErrorCode.RESOURCE_NOT_FOUND;
    }
}
