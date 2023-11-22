package rhetorike.glot.domain._1auth.service.orgnamesearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.service.orgnamesearch.academy.AcademySearcher;
import rhetorike.glot.domain._1auth.service.orgnamesearch.school.SchoolSearcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgNameService {
    private final SchoolSearcher schoolSearcher;
    private final AcademySearcher academySearcher;

    /**
     * 기관을 검색합니다.
     *
     * @param keyword 검색어
     * @return 기관명
     */
    public List<String> searchName(String keyword) {
        ArrayList<String> searchResult = new ArrayList<>();
        searchResult.addAll(schoolSearcher.getSchoolNames(keyword));
        searchResult.addAll(academySearcher.getAcademyNames(keyword));
        searchResult.sort((orgName1, orgName2) -> compareOrgName(orgName1, orgName2, keyword));
        return searchResult;
    }

    private int compareOrgName(String orgName1, String orgName2, String keyword){
        int num = Integer.compare(orgName1.indexOf(keyword), orgName2.indexOf(keyword));
        if (num == 0) {
            return orgName1.compareTo(orgName2);
        }
        return num;
    }
}
