package rhetorike.glot.domain._5faq.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import rhetorike.glot.domain._5faq.entity.Faq;
import rhetorike.glot.domain._5faq.FaqType;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FaqDto {

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CreationRequest {
        private FaqType type;
        private String title;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UpdateRequest {
        private FaqType type;
        private String title;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class GetRequest {
        private FaqType typeFilter;
        private FaqSearchType searchType;
        private String keyword;
        private Integer page;
        private Integer size;

        public enum FaqSearchType {
            TITLE,
            CONTENT,
            TITLE_AND_CONTENT
        }

    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private long id;
        private FaqType type;
        private String title;
        private String content;
        private String createdAt;
        private String lastModifiedAt;

        public Response(Faq faq) {
            this.id = faq.getId();
            this.type = faq.getType();
            this.title = faq.getTitle();
            this.content = faq.getContent();
            this.createdAt = faq.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.lastModifiedAt = faq.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PageResponse {
        private int totalPage;
        private List<Response> faqList;
    }

    @Getter
    @AllArgsConstructor
    public static class FagTypeResponse {
        private FaqType type;
        private String displayName;

        public FagTypeResponse(FaqType type) {
            this.type = type;
            this.displayName = type.getDisplayName();
        }
    }
}
