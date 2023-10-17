package rhetorike.glot.global.error.exception;

import lombok.Getter;
import rhetorike.glot.global.error.ErrorCode;

@Getter
public class MailingFailedException extends GlotException {
    private final ErrorCode errorCode;

    public MailingFailedException() {
        super();
        errorCode = ErrorCode.MAILING_FAILED;
    }
}
