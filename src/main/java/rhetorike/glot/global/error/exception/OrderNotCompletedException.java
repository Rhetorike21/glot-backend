package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class OrderNotCompletedException extends GlotUncheckedException {
    private final ErrorCode errorCode;

    public OrderNotCompletedException() {
        super();
        this.errorCode = ErrorCode.ORDER_NOT_COMPLETED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
