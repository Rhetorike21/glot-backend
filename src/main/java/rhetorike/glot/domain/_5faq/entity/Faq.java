package rhetorike.glot.domain._5faq.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._5faq.FaqType;
import rhetorike.glot.domain._5faq.dto.FaqDto;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity(name = "faq_table")
public class Faq extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    protected FaqType type;

    @Column(name = "title", length = 200)
    protected String title;

    @Column(name = "content", length = 50000)
    protected String content;

    @Builder
    public Faq(FaqType type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public void update(FaqDto.UpdateRequest requestDto) {
        if(requestDto.getTitle() != null) {
            this.title = requestDto.getTitle();
        }
        if(requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }
        if(requestDto.getType() != null) {
            this.type = requestDto.getType();
        }
    }
}
