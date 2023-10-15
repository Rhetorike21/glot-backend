package rhetorike.glot.domain._1auth.service.orgnamesearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SchoolSearcher{
    private final SchoolSearchStrategy schoolSearchStrategy;
    public List<String> getSchoolNames(String keyword) {
        return schoolSearchStrategy.search();
    }
}
