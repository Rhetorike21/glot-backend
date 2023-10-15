package rhetorike.glot.domain._1auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rhetorike.glot.domain._1auth.service.orgnamesearch.OrgNameService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrgNameController {
    private final OrgNameService orgNameService;
    public final static String SEARCH_URI = "/api/search/org";

    @GetMapping(SEARCH_URI)
    public ResponseEntity<List<String>> searchName(@RequestParam String keyword){
        List<String> result = orgNameService.searchName(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
