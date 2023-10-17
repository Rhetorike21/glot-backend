package rhetorike.glot.global.util.email;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EmailTest {


    @Test
    @DisplayName("아이디의 마지막 2글자를 *로 치환하고, <br>태그로 이어붙인다.")
    void replaceAndJoining(){
        //given
        List<String> accountIds = List.of("abc1234", "def5678", "ghi9012");

        //when
        String result = accountIds.stream()
                .map(str -> str.substring(0, str.length() - 2) + "**")
                .collect(Collectors.joining("<br>"));


        //then
        Assertions.assertThat(result).isEqualTo("abc12**<br>def56**<br>ghi90**");
    }



}