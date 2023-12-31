package rhetorike.glot.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("9999", "서버 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("0001", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("0002", "유효성 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CONNECTION_FAILED("0004", "외부 API 통신 중에 오류가 발생헀습니다. ", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("0005", "해당 리소스를 찾을 수 없습니다. ", HttpStatus.NOT_FOUND),
    WRONG_FORMAT_JWT("0006", "지원하지 않는 토큰 형식입니다.", HttpStatus.BAD_REQUEST),
    JWT_EXPIRED("0007", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("0008", "만료된 리프레시 토큰입니다. 다시 로그인해주세요", HttpStatus.UNAUTHORIZED),
    MOUNTAIN_NOT_FOUND("0009", "해당 산을 조회할 수 없습니다.", HttpStatus.NOT_FOUND),
    REISSUE_FAILED("0010", "토큰 재발급에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_FAILED("0011", "본인인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD("0012", "잘못된 비밀번호입니다..", HttpStatus.UNAUTHORIZED),
    JWT_BLOCKED("0013", "사용이 중지된 토큰입니다.", HttpStatus.BAD_REQUEST),
    USER_EXIST("0014", "이미 해당 회원이 존재합니다.", HttpStatus.BAD_REQUEST),
    MAILING_FAILED("0015", "이메일 전송에 실패했습니다.", HttpStatus.BAD_REQUEST),
    ILLEGAL_ARGUMENT("0016", "잘못된 파라미터입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("0017", "접근 권한이 없습니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_COMPLETED("0018", "주문이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED("0019", "결제에 실패했습니다. ", HttpStatus.BAD_REQUEST),
    REFUND_DENIED("0020", "환불이 불가능합니다.", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_REQUIRED("0021", "구독하지 않았거나, 만료되어 이용이 제한되었습니다.", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_ONGOING("0022", "이미 구독 중인 요금제가 존재합니다.", HttpStatus.BAD_REQUEST),
    INACTIVE_USER("0023", "비활성화된 사용자입니다.", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("0024", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}