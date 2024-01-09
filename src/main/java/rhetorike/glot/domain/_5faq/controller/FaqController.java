package rhetorike.glot.domain._5faq.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rhetorike.glot.domain._5faq.FaqType;
import rhetorike.glot.domain._5faq.dto.FaqDto;
import rhetorike.glot.domain._5faq.service.FaqService;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;
import rhetorike.glot.global.util.dto.SingleParamDto;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FaqController extends BaseTimeEntity {

    public static final String GET_FAQ_URI = "/api/faq";
    public static final String GET_FAQ_BY_ID_URI = "/api/faq/{faqId}";

    private final FaqService faqService;

    @PermitAll
    @GetMapping(GET_FAQ_URI)
    public ResponseEntity<FaqDto.PageResponse> getFaq(
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "type_filter", required = false) String typeFilter,
            @RequestParam(name = "search_type", required = false) String searchType,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        FaqDto.GetRequest requestDto = new FaqDto.GetRequest(
                typeFilter == null ? null : FaqType.valueOf(typeFilter),
                searchType == null ? null : FaqDto.GetRequest.FaqSearchType.valueOf(searchType),
                keyword,
                page,
                size
        );
        return new ResponseEntity<>(faqService.findAllFaq(requestDto), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping(GET_FAQ_BY_ID_URI)
    public ResponseEntity<FaqDto.Response> getFaqById(@PathVariable Long faqId) {
        return new ResponseEntity<>(faqService.findFaq(faqId), HttpStatus.OK);
    }
}