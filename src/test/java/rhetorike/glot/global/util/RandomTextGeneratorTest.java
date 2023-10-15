package rhetorike.glot.global.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class RandomTextGeneratorTest {

    @Test
    @DisplayName("무작위의 네자리 수를 생성한다.")
    void generateFourNumbers(){
        //given
        RandomTextGenerator randomTextGenerator = new RandomTextGenerator();

        //when
        String numbers = randomTextGenerator.generateFourNumbers();
        log.info(numbers);

        //then
        assertThat(numbers.length()).isEqualTo(4);
        assertThat(numbers.chars().allMatch(Character::isDigit)).isTrue();
    }
}