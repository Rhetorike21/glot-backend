package rhetorike.glot.domain._3writing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.domain._3writing.repository.WritingBoardRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;

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
    public void createBoard(WritingDto.CreationRequest requestDto, Long userId) {
        if (userId == null) {
            throw new UserNotFoundException();
        }
        requestDto.setTitleIfEmpty();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (WritingBoard.MAX_BOARD_LIMIT <= user.countBoard()) {
            writingBoardRepository.deleteLastModified();
        }
        WritingBoard writingBoard = WritingBoard.from(requestDto, user);
        writingBoardRepository.save(writingBoard);
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
}
