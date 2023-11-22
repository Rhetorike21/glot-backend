package rhetorike.glot.global.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CompositeTypeRegistration;
import org.springframework.stereotype.Component;

@Component
public class RandomTextGenerator {

    public String generateFourNumbers(){
        return RandomStringUtils.randomNumeric(4);
    }

    public String generateSixNumbers(){
        return RandomStringUtils.randomNumeric(6);
    }
}
