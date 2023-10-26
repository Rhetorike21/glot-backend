package rhetorike.glot.domain._3writing.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class WritingBoardDto {
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
        private long id;
        private String title;
        private YearMonth yearMonth;

        public Response(WritingBoard writingBoard) {
            this.id = writingBoard.getId();
            this.title = writingBoard.getTitle();
            this.yearMonth = YearMonth.from(writingBoard.getModifiedTime());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DetailResponse {
        private String title;
        private String content;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;

        public DetailResponse(WritingBoard writingBoard) {
            this.title = writingBoard.getTitle();
            this.content = writingBoard.getContent();
            this.createdTime = writingBoard.getCreatedTime();
            this.modifiedTime = writingBoard.getModifiedTime();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoveRequest {
        private long targetId;
        private long destinationId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
    }
}
