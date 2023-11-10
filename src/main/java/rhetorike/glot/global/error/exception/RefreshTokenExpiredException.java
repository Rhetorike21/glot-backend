package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class RefreshTokenExpiredException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public RefreshTokenExpiredException() {
        super();
        this.errorCode = ErrorCode.REFRESH_TOKEN_EXPIRED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
