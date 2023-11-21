package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class SubscriptionOngoingException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public SubscriptionOngoingException() {
        super();
        errorCode = ErrorCode.SUBSCRIPTION_ONGOING;
    }
}
