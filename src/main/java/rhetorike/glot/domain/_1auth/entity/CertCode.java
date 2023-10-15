package rhetorike.glot.domain._1auth.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CertCode {
    private final String pinNumbers;
    private boolean checked;

    public void setChecked() {
        this.checked = true;
    }
}
