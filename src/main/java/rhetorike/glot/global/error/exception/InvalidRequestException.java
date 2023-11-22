package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class InvalidRequestException extends GlotDetailedException {
    private final ErrorCode errorCode;
    private final String detailedMessage;

    public InvalidRequestException(String detailedMessage) {
        super();
        this.errorCode = ErrorCode.INVALID_REQUEST;
        this.detailedMessage = detailedMessage;
    }

    @Override
    public String getDetailMessage() {
        return this.detailedMessage;
    }
}
