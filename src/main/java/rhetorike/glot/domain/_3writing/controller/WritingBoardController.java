package rhetorike.glot.domain._3writing.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.service.WritingBoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WritingBoardController {
    public final static String CREATE_WRITING_BOARD_URI = "/api/writing";
    public final static String GET_WRITING_BOARD_URI = "/api/writing";

    private final WritingBoardService writingBoardService;

    @PermitAll
    @PostMapping(CREATE_WRITING_BOARD_URI)
    public ResponseEntity<Void> create(@RequestBody WritingDto.CreationRequest requestDto, @AuthenticationPrincipal Long userId) {
        writingBoardService.createBoard(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @GetMapping(GET_WRITING_BOARD_URI)
    public ResponseEntity<List<WritingDto.Response>> getAllBoards(@AuthenticationPrincipal Long userId) {
        List<WritingDto.Response> responseBody = writingBoardService.getAllBoards(userId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
