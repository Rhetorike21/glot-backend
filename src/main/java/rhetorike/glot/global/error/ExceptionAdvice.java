package rhetorike.glot.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rhetorike.glot.global.error.exception.GlotDetailedException;
import rhetorike.glot.global.error.exception.GlotException;
import rhetorike.glot.global.error.exception.InternalServerException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorResponseDto> handle(BindException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        e.printStackTrace();

        List<FieldErrorDto> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.add(new FieldErrorDto(field, message));
        });
        log.error("ExceptionAdvice - BindException 예외 발생");
        return new ResponseEntity<>(new ValidationErrorResponseDto(errorCode, errors), errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handle(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        e.printStackTrace();

        List<FieldErrorDto> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.add(new FieldErrorDto(field, message));
        });
        log.error("ExceptionAdvice - MethodArgumentNotValidException 예외 발생");
        return new ResponseEntity<>(new ValidationErrorResponseDto(errorCode, errors), errorCode.getHttpStatus());
    }

    @ExceptionHandler(InterruptedException.class)
    protected ResponseEntity<ErrorResponseDto> handle(InterruptedException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorResponseDto(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseDto> handle(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorResponseDto(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(InternalServerException.class)
    protected ResponseEntity<ErrorResponseDto> handle(InternalServerException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.getOriginalException().printStackTrace();
        return new ResponseEntity<>(new ErrorResponseDto(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(GlotException.class)
    protected ResponseEntity<ErrorResponseDto> handle(GlotException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorResponseDto(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(GlotDetailedException.class)
    protected ResponseEntity<ErrorResponseDto> handle(GlotDetailedException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorResponseDto(errorCode, e.getDetailMessage()), errorCode.getHttpStatus());
    }
}