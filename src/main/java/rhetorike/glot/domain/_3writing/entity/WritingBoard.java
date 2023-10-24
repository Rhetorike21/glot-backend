package rhetorike.glot.domain._3writing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
public class WritingBoard extends BaseTimeEntity {
    public final static int MAX_BOARD_LIMIT = 100;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40)
    private String title;

    private long sequence;

    @ManyToOne
    @JoinColumn
    private User user;

    @Builder
    public WritingBoard(Long id, String title, User user, long sequence, LocalDateTime createdTime, LocalDateTime modifiedTime) {
        this.id = id;
        this.title = title;
        this.sequence = sequence;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        changeUser(user);
    }

    public void changeUser(User user) {
        if (user == null) {
            return;
        }
        if (this.user != null) {
            user.getWritingBoards().remove(this);
        }
        this.user = user;
        user.getWritingBoards().add(this);
    }

    public static WritingBoard from(WritingDto.CreationRequest dto, User user) {
        long lastSequence = getLastSequence(user);
        WritingBoard writingBoard = WritingBoard.builder()
                .title(dto.getTitle())
                .sequence(lastSequence + 1)
                .build();
        writingBoard.changeUser(user);
        return writingBoard;
    }

    private static long getLastSequence(User user) {
        return user.getWritingBoards().stream()
                .mapToLong(WritingBoard::getSequence)
                .max().orElse(0L);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        WritingBoard that = (WritingBoard) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
