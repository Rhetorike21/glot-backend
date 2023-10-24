package rhetorike.glot.domain._3writing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class WritingDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CreationRequest {
        private String title;

        public void setTitleIfEmpty() {
            if (this.title == null || this.title.isBlank()) {
                this.title = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        String title;
        YearMonth yearMonth;

        public Response(WritingBoard writingBoard) {
            this.title = writingBoard.getTitle();
            this.yearMonth = YearMonth.from(writingBoard.getModifiedTime());
        }
    }
}
