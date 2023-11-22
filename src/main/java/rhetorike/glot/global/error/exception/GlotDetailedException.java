package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public abstract class GlotDetailedException extends RuntimeException{

    public GlotDetailedException() {
        super();
    }

    abstract public ErrorCode getErrorCode();
    abstract public String getDetailMessage();
}
