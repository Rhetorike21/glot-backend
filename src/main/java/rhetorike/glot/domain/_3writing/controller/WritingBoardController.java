package rhetorike.glot.domain._3writing.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._3writing.dto.WritingBoardDto;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WritingBoardController {
    public final static String GET_ALL_WRITING_BOARD_URI = "/api/writing";
    public final static String GET_WRITING_BOARD_URI = "/api/writing/{writingId}";
    public final static String DELETE_WRITING_BOARD_URI = "/api/writing/{writingId}";
    public final static String MOVE_BOARD_URI = "/api/writing/move";
    public final static String SAVE_BOARD_URI = "/api/writing";

    private final WritingBoardService writingBoardService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping(SAVE_BOARD_URI)
    public ResponseEntity<SingleParamDto<Long>> saveBoard(@AuthenticationPrincipal Long userId, @RequestBody WritingBoardDto.SaveRequest requestDto) {
        SingleParamDto<Long> responseBody = writingBoardService.saveBoard(userId, requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(GET_ALL_WRITING_BOARD_URI)
    public ResponseEntity<List<WritingBoardDto.Response>> getAllBoards(@AuthenticationPrincipal Long userId) {
        List<WritingBoardDto.Response> responseBody = writingBoardService.getAllBoards(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(GET_WRITING_BOARD_URI)
    public ResponseEntity<WritingBoardDto.DetailResponse> getBoard(@PathVariable Long writingId, @AuthenticationPrincipal Long userId) {
        WritingBoardDto.DetailResponse responseBody = writingBoardService.getBoard(userId, writingId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(DELETE_WRITING_BOARD_URI)
    public ResponseEntity<Void> deleteBoard(@PathVariable Long writingId, @AuthenticationPrincipal Long userId) {
        writingBoardService.deleteBoard(userId, writingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(MOVE_BOARD_URI)
    public ResponseEntity<Void> moveBoard(@RequestBody WritingBoardDto.MoveRequest requestDto, @AuthenticationPrincipal Long userId) {
        writingBoardService.moveBoard(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
