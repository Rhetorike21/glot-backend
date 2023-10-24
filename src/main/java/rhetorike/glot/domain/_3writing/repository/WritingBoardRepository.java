package rhetorike.glot.domain._3writing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.util.List;

public interface WritingBoardRepository extends JpaRepository<WritingBoard, Long> {
    @Modifying
    @Query(" delete from WritingBoard w where w.modifiedTime = (select min(w2.modifiedTime) from WritingBoard w2) ")
    void deleteLastModified();

    @Query(" select w from WritingBoard w where w.user = :user order by w.sequence desc ")
    List<WritingBoard > findByUserOrderBySequenceDesc(@Param("user") User user);
}
