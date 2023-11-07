package rhetorike.glot.domain._3writing.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.setup.IntegrationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RhetorikeWritingHelperTest extends IntegrationTest {
    @Autowired
    RhetorikeWritingHelper writingHelper;

    @Test
    @DisplayName("PROGRESS")
    void progress() {
        //given
        WritingHelper.Type type = WritingHelper.Type.PROGRESS;
        String sentence = "나는 밥을 먹었다.";

        //when
        List<String> help = writingHelper.help(type, sentence);
        log.info("{}", help);

        //then
    }

    @Test
    @DisplayName("REVERSE")
    void reverse() {
        //given
        WritingHelper.Type type = WritingHelper.Type.REVERSE;
        String sentence = "나는 밥을 먹었다.";

        //when
        List<String> help = writingHelper.help(type, sentence);
        log.info("{}", help);

        //then
    }

    @Test
    @DisplayName("CONCLUSION")
    void conclusion() {
        //given
        WritingHelper.Type type = WritingHelper.Type.CONCLUSION;
        String sentence = "나는 밥을 먹었다.";

        //when
        List<String> help = writingHelper.help(type, sentence);
        log.info("{}", help);

        //then
    }
}