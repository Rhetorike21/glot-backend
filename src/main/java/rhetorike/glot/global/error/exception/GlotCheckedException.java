package rhetorike.glot.global.error.exception;


import rhetorike.glot.global.error.ErrorCode;

public abstract class GlotCheckedException extends Exception{

    public GlotCheckedException() {
        super();
    }

    abstract public ErrorCode getErrorCode();
}
