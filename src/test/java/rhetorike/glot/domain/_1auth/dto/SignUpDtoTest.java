package rhetorike.glot.domain._1auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SignUpDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @ParameterizedTest
    @CsvSource(value = {"abc1234,0", "ab1,1", "ab11111111111111111111111,1", "abc1234!,1", "Abc1234,1"})
    @DisplayName("아이디 형식에 부합하지 않는 경우 예외가 발생한다.")
    void validateAccountId(String accountId, int expected) {
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest(accountId, "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com",  true, "1234");
        Set<ConstraintViolation<SignUpDto.PersonalRequest>> violations = validator.validate(requestDto);
        assertThat(violations.size()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"Abcd1234,0"})
    @DisplayName("비밀번호 형식에 부합하지 않는 경우 예외가 발생한다.")
    void validatePassword(String password, int expected) {
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest("hello1485", password, "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com",  true, "1234");
        Set<ConstraintViolation<SignUpDto.PersonalRequest>> violations = validator.validate(requestDto);
        assertThat(violations.size()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"valid@naver.com,0", "aaa,1"})
    @DisplayName("이메일 형식에 부합하지 않는 경우 예외가 발생한다.")
    void validateEmail(String email, int expected) {
        SignUpDto.PersonalRequest requestDto = new SignUpDto.PersonalRequest("hello1485", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", email,  true, "1234");
        Set<ConstraintViolation<SignUpDto.PersonalRequest>> violations = validator.validate(requestDto);
        assertThat(violations.size()).isEqualTo(expected);
    }
}