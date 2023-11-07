package rhetorike.glot.domain._3writing.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._3writing.dto.WritingHelpDto;
import rhetorike.glot.setup.ServiceTest;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class WritingHelpServiceTest {
    @InjectMocks
    WritingHelpService writingHelpService;

    @Mock
    WritingHelper writingHelper;

    @Test
    @DisplayName("[AI 작문 추천] - 발전형")
    void progressWriting(){
        //given
        String sentence = "작문 내용";
        String writingResult = "작문 결과";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, "progress");
        given(writingHelper.help(WritingHelper.Type.PROGRESS, sentence)).willReturn(List.of(writingResult));

        //when
        writingHelpService.write(requestDto);

        //then
        verify(writingHelper).help(WritingHelper.Type.PROGRESS, sentence);

    }

    @Test
    @DisplayName("[AI 작문 추천] - 반대형")
    void reverseWriting(){
        //given
        String sentence = "작문 내용";
        String writingResult = "작문 결과";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, "reverse");
        given(writingHelper.help(WritingHelper.Type.REVERSE, sentence)).willReturn(List.of(writingResult));

        //when
        writingHelpService.write(requestDto);

        //then
        verify(writingHelper).help(WritingHelper.Type.REVERSE, sentence);
    }

    @Test
    @DisplayName("[AI 작문 추천] - 결론형")
    void conclusionWriting(){
        //given
        String sentence = "작문 내용";
        String writingResult = "작문 결과";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, "conclusion");
        given(writingHelper.help(WritingHelper.Type.CONCLUSION, sentence)).willReturn(List.of(writingResult));

        //when
        writingHelpService.write(requestDto);

        //then
        verify(writingHelper).help(WritingHelper.Type.CONCLUSION, sentence);
    }

    @Test
    @DisplayName("[AI 작문 추천] - type 잘못된 경우 예외 발생")
    void WritingWrongType(){
        //given
        String sentence = "작문 내용";
        WritingHelpDto.Request requestDto = new WritingHelpDto.Request(sentence, "unavailable");

        //when

        //then
        Assertions.assertThatThrownBy(() -> writingHelpService.write(requestDto)).isInstanceOf(IllegalArgumentException.class);
    }
}