package rhetorike.glot.domain._1auth.service.orgnamesearch.academy;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface AcademySearchStrategy {
    List<String> search(String keyword);
}
