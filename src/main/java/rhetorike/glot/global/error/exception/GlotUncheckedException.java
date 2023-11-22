package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public abstract class GlotUncheckedException extends RuntimeException{

    public GlotUncheckedException() {
        super();
    }

    abstract public ErrorCode getErrorCode();
}
