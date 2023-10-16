package rhetorike.glot.domain._1auth.service.orgnamesearch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import rhetorike.glot.domain._1auth.service.orgnamesearch.academy.AcademySearcher;
import rhetorike.glot.domain._1auth.service.orgnamesearch.school.SchoolSearcher;
import rhetorike.glot.setup.ServiceTest;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ServiceTest
class OrgNameServiceTest {
    @InjectMocks
    OrgNameService orgNameService;

    @Mock
    SchoolSearcher schoolSearcher;

    @Mock
    AcademySearcher academySearcher;


    @Test
    @DisplayName("키워드로 기관을 검색한다.")
    void search() {
        //given
        String keyword = "한국";
        given(schoolSearcher.getSchoolNames(keyword)).willReturn(List.of("한국초등학교", "한국중학교", "한국고등학교"));
        given(academySearcher.getAcademyNames(keyword)).willReturn(List.of("한국교습소", "한국피아노", "한국영어"));

        //when
        List<String> result = orgNameService.searchName(keyword);

        //then
        Mockito.verify(schoolSearcher).getSchoolNames(keyword);
        Mockito.verify(academySearcher).getAcademyNames(keyword);
        Assertions.assertThat(result).containsExactly("한국고등학교","한국교습소","한국영어","한국중학교","한국초등학교","한국피아노");
    }
}