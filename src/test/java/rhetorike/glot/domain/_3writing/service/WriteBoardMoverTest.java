package rhetorike.glot.domain._3writing.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.setup.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Slf4j
class WriteBoardMoverTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    WritingBoardRepository writingBoardRepository;

    @Test
    @DisplayName("위로 이동")
    void moveUpper() {
        //given
        User user = Personal.builder().build();
        userRepository.save(user);

        WritingBoard wb1 = WritingBoard.builder().user(user).sequence(1).build();
        WritingBoard wb2 = WritingBoard.builder().user(user).sequence(2).build();
        WritingBoard wb3 = WritingBoard.builder().user(user).sequence(3).build();
        WritingBoard wb4 = WritingBoard.builder().user(user).sequence(4).build();
        WritingBoard wb5 = WritingBoard.builder().user(user).sequence(5).build();
        writingBoardRepository.saveAll(List.of(wb1, wb2, wb3, wb4, wb5));
        List<WritingBoard> before = writingBoardRepository.findByUserOrderBySequenceDesc(user);

        //when
        WriteBoardMover writeBoardMover = new WriteBoardMover();
        writeBoardMover.move(wb3, wb1, before);

        //then
        List<WritingBoard> result = writingBoardRepository.findByUserOrderBySequenceDesc(user);
        assertThat(result).containsExactly(wb5, wb4, wb2, wb1, wb3);
    }

    @Test
    @DisplayName("아래로 이동")
    void moveLower() {
        //given
        User user = Personal.builder().build();
        userRepository.save(user);

        WritingBoard wb1 = WritingBoard.builder().user(user).sequence(1).build();
        WritingBoard wb2 = WritingBoard.builder().user(user).sequence(2).build();
        WritingBoard wb3 = WritingBoard.builder().user(user).sequence(3).build();
        WritingBoard wb4 = WritingBoard.builder().user(user).sequence(4).build();
        WritingBoard wb5 = WritingBoard.builder().user(user).sequence(5).build();
        writingBoardRepository.saveAll(List.of(wb1, wb2, wb3, wb4, wb5));
        List<WritingBoard> boards = writingBoardRepository.findByUserOrderBySequenceDesc(user);

        //when
        WriteBoardMover writeBoardMover = new WriteBoardMover();
        writeBoardMover.move(wb4, wb2, boards);

        //then
        List<WritingBoard> result = writingBoardRepository.findByUserOrderBySequenceDesc(user);
        assertThat(result).containsExactly(wb5, wb3, wb2, wb4, wb1);
    }
}