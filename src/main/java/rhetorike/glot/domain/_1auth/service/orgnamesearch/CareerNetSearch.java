package rhetorike.glot.domain._1auth.service.orgnamesearch;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CareerNetSearch implements SchoolSearchStrategy{
    @Override
    public List<String> search() {
        return Collections.emptyList();
    }
}
