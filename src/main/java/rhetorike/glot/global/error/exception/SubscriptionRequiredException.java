package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class SubscriptionRequiredException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public SubscriptionRequiredException() {
        super();
        errorCode = ErrorCode.SUBSCRIPTION_REQUIRED;
    }
}
