package rhetorike.glot.domain._3writing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._4order.entity.Subscription;

import java.util.List;
import java.util.Optional;

public interface WritingBoardRepository extends JpaRepository<WritingBoard, Long> {
    @Modifying
    @Query(" delete from WritingBoard w where w.modifiedTime = (select min(w2.modifiedTime) from WritingBoard w2) ")
    void deleteLastModified();

    @Query(" select w from WritingBoard w where w.user = :user order by w.sequence desc ")
    List<WritingBoard > findByUserOrderBySequenceDesc(@Param("user") User user);

    @Query(" select w from WritingBoard w ")
    Optional<WritingBoard> findByUserRecent(@Param("user") User user);

    @Query(" select w from WritingBoard  w join User u on w.user = u join Subscription s on u.subscription = s where s = :subscription ")
    List<WritingBoard> findAllByMembers(@Param("subscription") Subscription subscription);

    Optional<WritingBoard> findByIdAndUser(Long id, User user);

    List<WritingBoard> findByUser(User user);
}
