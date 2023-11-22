package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class JwtExpiredException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public JwtExpiredException() {
        super();
        this.errorCode = ErrorCode.JWT_EXPIRED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
