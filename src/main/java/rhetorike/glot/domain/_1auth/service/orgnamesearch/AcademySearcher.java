package rhetorike.glot.domain._1auth.service.orgnamesearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AcademySearcher {
    private final AcademySearchStrategy academySearchStrategy;
    public List<String> getAcademyNames(String keyword) {
        return academySearchStrategy.search(keyword);
    }
}
