package rhetorike.glot.domain._5faq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FaqType {

    MAIN("메인페이지"),
    PRICING("Pricing"),
    ;

    private final String displayName;
}
