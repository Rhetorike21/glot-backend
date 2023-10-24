package rhetorike.glot.domain._3writing.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.service.WritingBoardService;

@RestController
@RequiredArgsConstructor
public class WritingBoardController {
    public final static String CREATE_WRITING_BOARD_URI = "/api/writing";
    private final WritingBoardService writingBoardService;

    @PermitAll
    @PostMapping(CREATE_WRITING_BOARD_URI)
    public ResponseEntity<Void> create(@RequestBody WritingDto.CreationRequest requestDto, @AuthenticationPrincipal Long userId) {
        writingBoardService.createBoard(requestDto, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
