package rhetorike.glot.domain._3writing.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.dto.SingleResponseDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WritingBoardService {
    private final WritingBoardRepository writingBoardRepository;
    private final UserRepository userRepository;
    private final WriteBoardMover writeBoardMover;

    /**
     * 작문 보드를 생성합니다.
     *
     * @param requestDto 제목
     */
    public SingleResponseDto<Long> createBoard(WritingDto.CreationRequest requestDto, Long userId) {
        if (userId == null) {
            throw new UserNotFoundException();
        }
        requestDto.setTitleIfEmpty();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (WritingBoard.MAX_BOARD_LIMIT <= user.countBoard()) {
            writingBoardRepository.deleteLastModified();
        }
        WritingBoard writingBoard = WritingBoard.from(requestDto, user);
        return new SingleResponseDto<>(writingBoardRepository.save(writingBoard).getId());
    }

    /**
     * 전체 작문 보드를 조회합니다.
     *
     * @param userId 회원 아이디넘버
     * @return 전체 작문 보드 목록
     */
    public List<WritingDto.Response> getAllBoards(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<WritingBoard> writingBoards = writingBoardRepository.findByUserOrderBySequenceDesc(user);
        return writingBoards.stream()
                .map(WritingDto.Response::new)
                .toList();
    }

    /**
     * 작문 보드 하나를 조회합니다.
     * 보드의 생성자가 아닌 경우, 예외가 발생합니다.
     *
     * @param userId         사용자 아이디넘버
     * @param writingBoardId 작문 보드 아이디넘버
     * @return 보드 정보
     */
    public WritingDto.DetailResponse getBoard(Long userId, Long writingBoardId) {
        User found = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        WritingBoard writingBoard = writingBoardRepository.findById(writingBoardId).orElseThrow(ResourceNotFoundException::new);
        if (writingBoard.getUser().equals(found)) {
            return new WritingDto.DetailResponse(writingBoard);
        }
        throw new AccessDeniedException();
    }

    /**
     * 작문 보드를 삭제합니다.
     *
     * @param userId         사용자 아이디넘버
     * @param writingBoardId 작문 보드 아이디넘버
     */
    public void deleteBoard(Long userId, Long writingBoardId) {
        User found = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        WritingBoard writingBoard = writingBoardRepository.findById(writingBoardId).orElseThrow(ResourceNotFoundException::new);
        if (writingBoard.getUser().equals(found)) {
            writingBoard.deleteUser();
            writingBoardRepository.delete(writingBoard);
            return;
        }
        throw new AccessDeniedException();
    }

    @Transactional
    public void moveBoard(WritingDto.MoveRequest requestDto, Long userId) {
        WritingBoard targetBoard = writingBoardRepository.findById(requestDto.getTargetId()).orElseThrow(ResourceNotFoundException::new);
        WritingBoard destinationBoard = writingBoardRepository.findById(requestDto.getDestinationId()).orElseThrow(ResourceNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (validate(targetBoard, destinationBoard, user)) {
            List<WritingBoard> writingBoards = writingBoardRepository.findByUserOrderBySequenceDesc(user);
            writeBoardMover.move(targetBoard, destinationBoard, writingBoards);
            return;
        }
        throw new AccessDeniedException();
    }

    private boolean validate(WritingBoard targetBoard, WritingBoard destinationBoard, User user) {
        return user.equals(targetBoard.getUser()) && user.equals(destinationBoard.getUser());
    }

    /**
     * 보드를 수정합니다.null이 아닌 항목만 변경됩니다.
     *
     * @param writingBoardId 수정할 보드 아이디넘버
     * @param userId         사용자 아이디넘버
     * @param requestDto     수정 사항
     */
    @Transactional
    public void updateBoard(Long writingBoardId, Long userId, WritingDto.UpdateRequest requestDto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        WritingBoard writingBoard = writingBoardRepository.findById(writingBoardId).orElseThrow(ResourceNotFoundException::new);
        if (user.equals(writingBoard.getUser())) {
            update(writingBoard, requestDto);
            return;
        }
        throw new AccessDeniedException();
    }

    private void update(WritingBoard writingBoard, WritingDto.UpdateRequest requestDto) {
        if (requestDto.getTitle() != null && !requestDto.getTitle().isBlank()) {
            writingBoard.setTitle(requestDto.getTitle());
        }
        if (requestDto.getContent() != null) {
            writingBoard.setContent(requestDto.getContent());
        }
    }
}
