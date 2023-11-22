package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public class PaymentFailedException extends GlotDetailedException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public PaymentFailedException(String detailMessage) {
        super();
        this.detailMessage = detailMessage;
        this.errorCode = ErrorCode.PAYMENT_FAILED;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    @Override
    public String getDetailMessage() {
        return detailMessage;
    }
}
