package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public abstract class GlotException extends RuntimeException{

    public GlotException() {
        super();
    }

    abstract public ErrorCode getErrorCode();
}
