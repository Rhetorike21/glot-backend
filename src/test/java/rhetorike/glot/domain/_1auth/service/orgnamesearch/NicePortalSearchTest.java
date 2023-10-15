package rhetorike.glot.domain._1auth.service.orgnamesearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NicePortalSearchTest {
    @Autowired
    NicePortalSearch nicePortalSearch;

    @Test
    @DisplayName("검색어로 학원을 검색한다.")
    void search() throws JsonProcessingException {
        //given
        String keyword = "영";
        List<String> result = nicePortalSearch.search(keyword);

        //when


        //then


    }


}