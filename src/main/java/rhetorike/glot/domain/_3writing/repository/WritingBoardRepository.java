package rhetorike.glot.domain._3writing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

public interface WritingBoardRepository extends JpaRepository<WritingBoard, Long> {
    @Modifying
    @Query(" delete from WritingBoard w where w.modifiedTime = (select min(w2.modifiedTime) from WritingBoard w2) ")
    void deleteLastModified();
}
