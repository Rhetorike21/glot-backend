package rhetorike.glot.domain._1auth.service.orgnamesearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
        searchResult.sort(getKeywordComparator(keyword));
        return searchResult;
    }
    private Comparator<String> getKeywordComparator(String keyword) {
        return (o1, o2) -> {
            int num = Integer.compare(o1.indexOf(keyword), o2.indexOf(keyword));
            if (num == 0) {
                return o1.compareTo(o2);
            }
            return num;
        };
    }
}
