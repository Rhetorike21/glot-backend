package rhetorike.glot.domain._3writing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._3writing.dto.WritingHelpDto;
import rhetorike.glot.global.util.dto.SingleParamDto;

@Service
@RequiredArgsConstructor
public class WritingHelpService {
    private final WritingHelper writingHelper;
    public WritingHelpDto.Response write(WritingHelpDto.Request requestDto){
        String sentence = requestDto.getSentence();
        WritingHelper.Type type = WritingHelper.Type.findByName(requestDto.getType());
        return new WritingHelpDto.Response(writingHelper.help(type, sentence));
    }
}
