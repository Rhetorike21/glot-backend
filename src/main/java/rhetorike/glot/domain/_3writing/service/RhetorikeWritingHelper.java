package rhetorike.glot.domain._3writing.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class RhetorikeWritingHelper implements WritingHelper{
    @Override
    public String help(Type type, String sentence) {
        String param = type.param();

        return "작문 결과";
    }
}
