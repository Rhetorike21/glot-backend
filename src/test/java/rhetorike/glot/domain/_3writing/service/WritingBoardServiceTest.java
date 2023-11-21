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
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.setup.ServiceTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("[작문 보드 저장] - 신규 보드 생성")
    void createBoardByUser() {
        //given
        final Long USER_ID = 1L;
        User user = Personal.builder().id(USER_ID).writingBoards(List.of()).subscription(new Subscription()).build();
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(null, "제목", "내용");
        WritingBoard writingBoard = WritingBoard.builder().id(1L).user(user).build();
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(writingBoardRepository.save(any())).willReturn(writingBoard);

        //when
        writingBoardService.saveBoard(USER_ID, requestDto);

        //then
        verify(writingBoardRepository).save(any());
    }

    @Test
    @DisplayName("[작문 보드 저장] - 생성된 보드가 제한 수량 이상인 경우, 가장 오래된 보드 삭제")
    void deleteLastModified() {
        //given
        final Long USER_ID = 1L;
        List<WritingBoard> writingBoards = new ArrayList<>();
        User user = Personal.builder().id(USER_ID).writingBoards(writingBoards).subscription(new Subscription()).build();
        for (int i = 0; i < 100; i++) {
            writingBoards.add(WritingBoard.builder().user(user).build());
        }
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(null, "제목", "내용");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(writingBoardRepository.save(any())).willReturn(WritingBoard.builder().id(1L).user(user).build());

        //when
        writingBoardService.saveBoard(USER_ID, requestDto);

        //then
        verify(userRepository).findById(USER_ID);
        verify(writingBoardRepository).deleteLastModified();
        verify(writingBoardRepository).save(any(WritingBoard.class));
    }


    @Test
    @DisplayName("[작문 보드 저장] - 기존 보드 수정")
    void updateBoard(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(writingBoardId, "수정할 제목", "수정할 내용");

        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.saveBoard(userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(writingBoardId, user);
    }

    @Test
    @DisplayName("[작문 보드 저장] - 기존 보드 제목 수정")
    void updateTitleOnly(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(writingBoardId, "수정할 제목", null);

        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.saveBoard(userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(writingBoardId, user);
    }

    @Test
    @DisplayName("[작문 보드 저장] - 기존 보드 내용 수정")
    void updateContentOnly(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        WritingBoardDto.SaveRequest requestDto = new WritingBoardDto.SaveRequest(writingBoardId, null, "수정할 내용");

        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.saveBoard(userId, requestDto);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(writingBoardId, user);
    }

    @Test
    @DisplayName("[전체 작문 보드 조회]")
    void getAllBoards(){
        //given
        final long userId = 1L;
        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
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
        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.getBoard(userId, writingBoardId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(writingBoardId, user);
    }

    @Test
    @DisplayName("[작문 보드 조회] - 다른 사람의 보드를 조회하려 하는 경우 예외 발생")
    void getBoardThrowAccessDeniedException(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.empty());

        //when

        //then
        Assertions.assertThatThrownBy(() -> writingBoardService.getBoard(userId, writingBoardId)).isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    @DisplayName("[작문 보드 삭제]")
    void delete(){
        //given
        final long userId = 1L;
        final long writingBoardId = 1L;
        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard writingBoard = WritingBoard.builder().id(writingBoardId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(writingBoardId, user)).willReturn(Optional.of(writingBoard));

        //when
        writingBoardService.deleteBoard(userId, writingBoardId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(writingBoardId, user);
    }

    @Test
    @DisplayName("[작문 보드 이동]")
    void moveBoard(){
        //given
        final long userId = 1L;
        final long targetId = 1L;
        final long destinationId = 2L;
        User user = Personal.builder().id(userId).subscription(new Subscription()).build();
        WritingBoard targetBoard = WritingBoard.builder().id(targetId).user(user).build();
        WritingBoard destinationBoard = WritingBoard.builder().id(destinationId).user(user).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(writingBoardRepository.findByIdAndUser(targetId, user)).willReturn(Optional.of(targetBoard));
        given(writingBoardRepository.findByIdAndUser(destinationId, user)).willReturn(Optional.of(destinationBoard));
        WritingBoardDto.MoveRequest moveRequest = new WritingBoardDto.MoveRequest(1L, 2L);

        //when
        writingBoardService.moveBoard(moveRequest, userId);

        //then
        verify(userRepository).findById(userId);
        verify(writingBoardRepository).findByIdAndUser(targetId, user);
        verify(writingBoardRepository).findByIdAndUser(destinationId, user);

    }


    @Test
    @DisplayName("작문 기능을 이용한 적이 있는지 확인한다. ")
    void hasUsedBoard(){
        //given
        Subscription subscription = new Subscription();
        given(writingBoardRepository.findAllByMembers(subscription)).willReturn(List.of(new WritingBoard()));

       //when
        boolean result = writingBoardService.hasUsedBoard(subscription);

        //then
        verify(writingBoardRepository).findAllByMembers(subscription);
        assertThat(result).isTrue();
    }



}