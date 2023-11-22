package rhetorike.glot.domain._3writing.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class WritingHelpDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Request{
        private String sentence;
        private String type;
    }
    @Getter
    @AllArgsConstructor
    public static class Response{
        private List<String> result;
    }
}
