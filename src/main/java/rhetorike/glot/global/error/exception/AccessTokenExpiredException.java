package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class AccessTokenExpiredException extends GlotException {
    private final ErrorCode errorCode;

    public AccessTokenExpiredException() {
        super();
        this.errorCode = ErrorCode.ACCESS_TOKEN_EXPIRED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
