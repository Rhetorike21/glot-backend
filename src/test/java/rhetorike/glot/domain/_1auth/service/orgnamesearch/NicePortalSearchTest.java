package rhetorike.glot.domain._1auth.service.orgnamesearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rhetorike.glot.domain._1auth.service.orgnamesearch.academy.NicePortalSearch;

import java.util.List;

@SpringBootTest
class NicePortalSearchTest {
    @Autowired
    NicePortalSearch nicePortalSearch;

    @Test
    @DisplayName("검색어로 학원을 검색한다.")
    void search()  {
        //given
        String keyword = "영어";

        //when
        List<String> result = nicePortalSearch.search(keyword);

        //then
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 검색어로 검색한다.")
    void searchNotExist() {
        //given
        String keyword = "sajslfj";

        //when
        List<String> result = nicePortalSearch.search(keyword);

        //then
        Assertions.assertThat(result).isEmpty();
    }
}