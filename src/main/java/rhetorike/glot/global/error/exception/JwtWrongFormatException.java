package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class JwtWrongFormatException extends GlotException {

    private final ErrorCode errorCode;

    public JwtWrongFormatException() {
        super();
        errorCode = ErrorCode.WRONG_FORMAT_JWT;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
