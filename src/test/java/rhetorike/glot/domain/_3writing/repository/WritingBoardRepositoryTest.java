package rhetorike.glot.domain._3writing.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.setup.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RepositoryTest
class WritingBoardRepositoryTest {

    @Autowired
    WritingBoardRepository writingBoardRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("WritingBoard를 저장하고, 조회한다.")
    void saveAndFind() {
        //given
        WritingBoard saved = writingBoardRepository.save(WritingBoard.builder().user(Personal.builder().build()).build());

        //when
        Optional<WritingBoard> found = writingBoardRepository.findById(saved.getId());

        //then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("User를 지정하여 저장한다.")
    void saveUser() {
        //given
        User user = userRepository.save(Personal.builder().build());
        WritingBoard saved = writingBoardRepository.save(WritingBoard.builder().user(user).build());

        //when
        Optional<WritingBoard> found = writingBoardRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getUser()).isEqualTo(user);
    }


    @Test
    @DisplayName("WritingBoard를 삭제한다.")
    void delete() {
        //given
        WritingBoard saved = writingBoardRepository.save(WritingBoard.builder().user(Personal.builder().build()).build());

        //when
        writingBoardRepository.delete(saved);
        Optional<WritingBoard> found = writingBoardRepository.findById(saved.getId());

        //then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("가장 오래된 WritingBoard를 삭제한다.")
    void deleteLastModified() throws InterruptedException {
        //given
        User user = userRepository.save(Personal.builder().build());
        WritingBoard writingBoard1 = writingBoardRepository.save(WritingBoard.builder().user(user).build());
        sleep(1L);
        WritingBoard writingBoard2 = writingBoardRepository.save(WritingBoard.builder().user(user).build());
        sleep(1L);
        WritingBoard writingBoard3 = writingBoardRepository.save(WritingBoard.builder().user(user).build());
        sleep(1L);
        WritingBoard writingBoard4 = writingBoardRepository.save(WritingBoard.builder().user(user).build());
        sleep(1L);

        //when
        writingBoardRepository.deleteLastModified();
        List<WritingBoard> writingBoards = writingBoardRepository.findAll();

        //then
        assertThat(writingBoards).hasSize(3);
        assertThat(writingBoards).containsExactlyInAnyOrder(writingBoard2, writingBoard3, writingBoard4);
    }


    @Test
    @DisplayName("WritingBoard와 User를 함께 저장한다.")
    void saveWritingBoardAndUser(){
        //given
        User user = userRepository.save(Personal.builder().build());
        writingBoardRepository.save(WritingBoard.builder().user(user).build());

        //when
        List<WritingBoard> writingBoards = user.getWritingBoards();

        //then
        assertThat(writingBoards).hasSize(1);
    }


    @Test
    @DisplayName("사용자가 보유한 모든 WritingBoard를 sequence 기준으로 내림차순 정렬한다.")
    void findByUserOrderBySequenceDesc(){
        //given
        User user = userRepository.save(Personal.builder().build());
        WritingBoard writingBoard1 = writingBoardRepository.save(WritingBoard.builder().user(user).sequence(2).build());
        WritingBoard writingBoard2 = writingBoardRepository.save(WritingBoard.builder().user(user).sequence(4).build());
        WritingBoard writingBoard3 = writingBoardRepository.save(WritingBoard.builder().user(user).sequence(3).build());
        WritingBoard writingBoard4 = writingBoardRepository.save(WritingBoard.builder().user(user).sequence(1).build());

        //when
        List<WritingBoard> result = writingBoardRepository.findByUserOrderBySequenceDesc(user);

        //then
        assertThat(result).containsExactly(writingBoard2, writingBoard3, writingBoard1, writingBoard4);
    }

    @Test
    @DisplayName("나중에 생성된 보드가 sequence가 높도록 설정된다.")
    void setSequence(){
        //given
        User user = userRepository.save(Personal.builder().build());
        WritingBoard writingBoard1 = writingBoardRepository.save(WritingBoard.from(new WritingBoardDto.CreationRequest("1"), user));
        WritingBoard writingBoard2 = writingBoardRepository.save(WritingBoard.from(new WritingBoardDto.CreationRequest("2"), user));
        WritingBoard writingBoard3 = writingBoardRepository.save(WritingBoard.from(new WritingBoardDto.CreationRequest("3"), user));
        WritingBoard writingBoard4 = writingBoardRepository.save(WritingBoard.from(new WritingBoardDto.CreationRequest("4"), user));

        //when
        List<WritingBoard> result = writingBoardRepository.findByUserOrderBySequenceDesc(user);

        //then
        assertThat(result).containsExactly(writingBoard4, writingBoard3, writingBoard2, writingBoard1);
        assertThat(result.stream().mapToLong(WritingBoard::getSequence).toArray()).containsExactly(4L, 3L, 2L, 1L);
    }
}