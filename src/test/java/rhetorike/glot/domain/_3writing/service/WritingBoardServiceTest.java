package rhetorike.glot.domain._3writing.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Mock
    WriteBoardMover writeBoardMover;


    @Test
    @DisplayName("[작문 보드 생성] - 회원")
    void createBoardByUser() {
        //given
        final Long USER_ID = 1L;
        User user = Personal.builder().id(USER_ID).writingBoards(List.of()).build();
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(writingBoardRepository.save(any())).willReturn(WritingBoard.builder().id(1L).user(user).build());


        //when
        writingBoardService.createBoard(requestDto, USER_ID);

        //then
        verify(userRepository).findById(USER_ID);
        verify(writingBoardRepository).save(any(WritingBoard.class));
        verify(writingBoardRepository).save(any());
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
        User user = Personal.builder().id(USER_ID).writingBoards(writingBoards).build();
        for (int i = 0; i < 100; i++) {
            writingBoards.add(WritingBoard.builder().user(user).build());
        }
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("제목");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(writingBoardRepository.save(any())).willReturn(WritingBoard.builder().id(1L).user(user).build());

        //when
        writingBoardService.createBoard(requestDto, USER_ID);

        //then
        verify(userRepository).findById(USER_ID);
        verify(writingBoardRepository).deleteLastModified();
        verify(writingBoardRepository).save(any(WritingBoard.class));
    }


    @Test
    @DisplayName("[전체 작문 보드 조회]")
    void getAllBoards(){
        //given
        final long userId = 1L;
        User user = Personal.builder().build();
        WritingBoard writingBoard = WritingBoard.builder().id(1L).title("제목").modifiedTime(LocalDateTime.now()).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByUserOrderBySequenceDesc(user)).willReturn(List.of(writingBoard));

        //when
        writingBoardService.getAllBoards(userId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByUserOrderBySequenceDesc(user);
    }

    @Test
    @DisplayName("[작문 보드 조회] ")
    void getBoard(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        User user = Personal.builder().id(userId).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.getBoard(userId, writingBoardId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(writingBoardId);
    }

    @Test
    @DisplayName("[작문 보드 조회] - 다른 사람의 보드를 조회하려 하는 경우 예외 발생")
    void getBoardThrowAccessDeniedException(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        User user = Personal.builder().id(userId).build();
        User other = Personal.builder().id(2L).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(other).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when

        //then
        Assertions.assertThatThrownBy(() -> writingBoardService.getBoard(userId, writingBoardId)).isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @DisplayName("[작문 보드 삭제]")
    void delete(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        User user = Personal.builder().id(userId).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.deleteBoard(userId, writingBoardId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(writingBoardId);
    }

    @Test
    @DisplayName("[작문 보드 이동]")
    void moveBoard(){
        //given
        final long userId = 1L;
        final long targetId = 1L;
        final long destinationId = 2L;
        User user = Personal.builder().id(userId).build();
        WritingBoard targetBoard = WritingBoard.builder().id(targetId).user(user).build();
        WritingBoard destinationBoard = WritingBoard.builder().id(destinationId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(targetId)).willReturn(Optional.of(targetBoard));
        given(writingBoardRepository.findById(destinationId)).willReturn(Optional.of(destinationBoard));
        WritingDto.MoveRequest moveRequest = new WritingDto.MoveRequest(1L, 2L);

        //when
        writingBoardService.moveBoard(moveRequest, userId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(targetId);
        verify(writingBoardRepository).findById(destinationId);
    }

    @Test
    @DisplayName("[작문 보드 수정]")
    void updateBoard(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingDto.UpdateRequest requestDto = new WritingDto.UpdateRequest("수정할 제목", "수정할 내용");

        User user = Personal.builder().id(userId).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.updateBoard(writingBoardId, userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(writingBoardId);
    }

    @Test
    @DisplayName("[작문 보드 수정] - 제목만 수정")
    void updateTitleOnly(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingDto.UpdateRequest requestDto = new WritingDto.UpdateRequest("수정할 제목", null);

        User user = Personal.builder().id(userId).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.updateBoard(writingBoardId, userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(writingBoardId);
    }

    @Test
    @DisplayName("[작문 보드 수정] - 내용만 수정")
    void updateContentOnly(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingDto.UpdateRequest requestDto = new WritingDto.UpdateRequest(null, "수정할 내용");

        User user = Personal.builder().id(userId).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findById(writingBoardId)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.updateBoard(writingBoardId, userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findById(writingBoardId);
    }
}