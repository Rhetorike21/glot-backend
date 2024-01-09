package rhetorike.glot.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._5faq.FaqType;
import rhetorike.glot.domain._5faq.dto.FaqDto;
import rhetorike.glot.domain._5faq.service.FaqService;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminFaqController {

    public static final String CREATE_FAQ_URI = "/api/faq";
    public static final String UPDATE_FAQ_URI = "/api/faq/{faqId}";
    public static final String DELETE_FAQ_URI = "/api/faq/{faqId}";
    public static final String GET_FAQ_TYPE_URI = "/api/faq/type";

    private final FaqService faqService;

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping(CREATE_FAQ_URI)
    public ResponseEntity<SingleParamDto<Long>> createFaq(@RequestBody FaqDto.CreationRequest requestDto) {
        validateRequestDto(requestDto);
        SingleParamDto<Long> responseBody = faqService.createFaq(requestDto);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(UPDATE_FAQ_URI)
    public ResponseEntity<SingleParamDto<Long>> updateFaq(@PathVariable Long faqId, @RequestBody FaqDto.UpdateRequest requestDto) {
        validateRequestDto(requestDto);
        faqService.updateFaq(faqId, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(DELETE_FAQ_URI)
    public ResponseEntity<SingleParamDto<Long>> deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping(GET_FAQ_TYPE_URI)
    public ResponseEntity<List<FaqDto.FagTypeResponse>> getFaqType() {
        return new ResponseEntity<>(Arrays.stream(FaqType.values()).map(FaqDto.FagTypeResponse::new).toList(), HttpStatus.OK);
    }

    private void validateRequestDto(FaqDto.CreationRequest requestDto) {
        if (requestDto.getType() == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (requestDto.getTitle() == null) {
            throw new IllegalArgumentException("title is null");
        }
        if (requestDto.getContent() == null) {
            throw new IllegalArgumentException("content is null");
        }
    }

    private void validateRequestDto(FaqDto.UpdateRequest requestDto) {
        if (requestDto.getType() == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (requestDto.getTitle() == null) {
            throw new IllegalArgumentException("title is null");
        }
        if (requestDto.getContent() == null) {
            throw new IllegalArgumentException("content is null");
        }
    }
}
