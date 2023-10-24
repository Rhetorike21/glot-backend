package rhetorike.glot.domain._3writing.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ServiceTest
class WritingBoardServiceTest {
    @InjectMocks
    WritingBoardService writingBoardService;

    @Mock
    WritingBoardRepository writingBoardRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("[작문 보드 생성] - 회원")
    void createBoardByUser() {
        //given
        final Long USER_ID = 1L;
        User user = Personal.builder().id(USER_ID).writingBoards(List.of()).build();
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        //when
        writingBoardService.createBoard(requestDto, USER_ID);

        //then
        verify(userRepository).findById(USER_ID);
        verify(writingBoardRepository).save(any(WritingBoard.class));
    }

    @Test
    @DisplayName("[작문 보드 생성] - 비회원")
    void createBoardByUnknown() {
        //given
        final Long USER_ID = null;
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");

        //then
        Assertions.assertThatThrownBy(() -> writingBoardService.createBoard(requestDto, USER_ID)).isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("[작문 보드 생성] - 현재 시간을 제목으로 설정")
    void test() {
        //given
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //when
        log.info(format);

        //then
    }

    @Test
    @DisplayName("[작문 보드 생성] - 생성된 보드가 제한 수량 이상인 경우, 가장 오래된 보드 삭제")
    void deleteLastModified() {
        //given
        final Long USER_ID = 1L;
        List<WritingBoard> writingBoards = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            writingBoards.add(new WritingBoard());
        }
        User user = Personal.builder().id(USER_ID).writingBoards(writingBoards).build();
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

        //when
        writingBoardService.createBoard(requestDto, USER_ID);

        //then
        verify(userRepository).findById(USER_ID);
        verify(writingBoardRepository).deleteLastModified();
        verify(writingBoardRepository).save(any(WritingBoard.class));
    }


    @Test
    @DisplayName("전체 작문 보드 조회")
    void getAllBoards(){
        //given
        final long userId = 1L;
        User user = Personal.builder().build();
        WritingBoard writingBoard = WritingBoard.builder().title("제목").modifiedTime(LocalDateTime.now()).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByUserOrderBySequenceDesc(user)).willReturn(List.of(writingBoard));

        //when
        writingBoardService.getAllBoards(userId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByUserOrderBySequenceDesc(user);
    }
}