package rhetorike.glot.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    PERSONAL("ROLE_PERSONAL"),
    ORGANIZATION("ROLE_ORG"),
    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN");
    private final String value;

    public String value(){
        return this.value;
    }
}
