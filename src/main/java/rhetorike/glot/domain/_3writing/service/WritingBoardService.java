package rhetorike.glot.domain._3writing.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.domain._4order.entity.Subscription;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.error.exception.ResourceNotFoundException;
import rhetorike.glot.global.error.exception.SubscriptionRequiredException;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WritingBoardService {
    private final WritingBoardRepository writingBoardRepository;
    private final UserRepository userRepository;
    private final WriteBoardMover writeBoardMover;

    /**
     * 전체 작문 보드를 조회합니다.
     *
     * @param userId 회원 아이디넘버
     * @return 전체 작문 보드 목록
     */
    public List<WritingBoardDto.Response> getAllBoards(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        validateSubscription(user);

        List<WritingBoard> writingBoards = writingBoardRepository.findByUserOrderBySequenceDesc(user);
        return writingBoards.stream()
                .map(WritingBoardDto.Response::new)
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
    public WritingBoardDto.DetailResponse getBoard(Long userId, Long writingBoardId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        validateSubscription(user);

        WritingBoard writingBoard = writingBoardRepository.findByIdAndUser(writingBoardId, user).orElseThrow(ResourceNotFoundException::new);
        return new WritingBoardDto.DetailResponse(writingBoard);
    }

    /**
     * 작문 보드를 삭제합니다.
     *
     * @param userId         사용자 아이디넘버
     * @param writingBoardId 작문 보드 아이디넘버
     */
    public void deleteBoard(Long userId, Long writingBoardId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        validateSubscription(user);

        WritingBoard writingBoard = writingBoardRepository.findByIdAndUser(writingBoardId, user).orElseThrow(ResourceNotFoundException::new);
        writingBoard.deleteUser();
        writingBoardRepository.delete(writingBoard);
    }

    @Transactional
    public void moveBoard(WritingBoardDto.MoveRequest requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        validateSubscription(user);

        WritingBoard targetBoard = writingBoardRepository.findByIdAndUser(requestDto.getTargetId(), user).orElseThrow(ResourceNotFoundException::new);
        WritingBoard destinationBoard = writingBoardRepository.findByIdAndUser(requestDto.getDestinationId(), user).orElseThrow(ResourceNotFoundException::new);
        List<WritingBoard> writingBoards = writingBoardRepository.findByUserOrderBySequenceDesc(user);
        writeBoardMover.move(targetBoard, destinationBoard, writingBoards);
    }


    /**
     * 보드를 저장합니다.null이 아닌 항목만 변경됩니다. id를 찾을 수 없는 경우, 새로 생성합니다.
     *
     * @param userId     사용자 아이디넘버
     * @param requestDto 수정 사항
     */
    @Transactional
    public SingleParamDto<Long> saveBoard(Long userId, WritingBoardDto.SaveRequest requestDto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        validateSubscription(user);

        if (requestDto.getWritingBoardId() == null){
            return new SingleParamDto<>(createNewBoard(user, requestDto).getId());
        }
        WritingBoard writingBoard = writingBoardRepository.findByIdAndUser(requestDto.getWritingBoardId(), user).orElseThrow(ResourceNotFoundException::new);
        writingBoard.update(requestDto);
        return new SingleParamDto<>(writingBoard.getId());
    }

    private WritingBoard createNewBoard(User user, WritingBoardDto.SaveRequest requestDto) {
        if (WritingBoard.MAX_BOARD_LIMIT <= user.countBoard()) {
            writingBoardRepository.deleteLastModified();
        }
        WritingBoard writingBoard = WritingBoard.from(requestDto, user);
        return writingBoardRepository.save(writingBoard);
    }

    public boolean hasUsedBoard(Subscription subscription) {
        List<WritingBoard> boards = writingBoardRepository.findAllByMembers(subscription);
        return !boards.isEmpty();
    }

    private void validateSubscription(User user){
        if (user.getSubscription() == null){
            throw new SubscriptionRequiredException();
        }
    }

    public void deleteAllBoardOfUser(User user){
        List<WritingBoard> writingBoards = writingBoardRepository.findByUser(user);
        writingBoardRepository.deleteAll(writingBoards);
    }
}
