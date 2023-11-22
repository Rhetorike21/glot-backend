package rhetorike.glot.global.error;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponseDto {
    private int status;
    private String code;
    private String name;
    private String message;

    public ErrorResponseDto(ErrorCode errorCode){
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.getCode();
        this.name = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponseDto(ErrorCode errorCode, String detailMessage){
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.getCode();
        this.name = errorCode.name();
        this.message = errorCode.getMessage() + " [" + detailMessage + "] ";
    }
}
