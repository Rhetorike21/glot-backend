package rhetorike.glot.domain._1auth.service.orgnamesearch.school;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CareerNetSearchTest {

    @Test
    @DisplayName("커리어넷 API로 학교명을 검색한다.")
    void searchName(){
        //given
        String keyword = "한국";
        CareerNetSearch careerNetSearch = new CareerNetSearch();

        //when
        List<String> result = careerNetSearch.search(keyword);
        log.info("{}", result);

        //then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 검색어로 검색한다.")
    void searchNameEmpty(){
        //given
        String keyword = "djsfljsadlf";
        CareerNetSearch careerNetSearch = new CareerNetSearch();

        //when
        List<String> result = careerNetSearch.search(keyword);
        log.info("{}", result);

        //then
        assertThat(result).isEmpty();
    }


}