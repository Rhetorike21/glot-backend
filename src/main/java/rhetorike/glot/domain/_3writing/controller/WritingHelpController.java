package rhetorike.glot.domain._3writing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._3writing.dto.WritingHelpDto;
import rhetorike.glot.domain._3writing.service.WritingHelpService;
import rhetorike.glot.global.util.dto.SingleParamDto;

@RestController
@RequiredArgsConstructor
public class WritingHelpController {
    public final static String WRITING_HELP_API = "/api/help/writing";
    private final WritingHelpService writingHelpService;

    @PostMapping(WRITING_HELP_API)
    public ResponseEntity<WritingHelpDto.Response> write(@RequestBody WritingHelpDto.Request requestDto) {
        WritingHelpDto.Response responseBody = writingHelpService.write(requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
