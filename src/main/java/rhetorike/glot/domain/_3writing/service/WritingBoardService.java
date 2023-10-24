package rhetorike.glot.domain._3writing.service;

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
}
