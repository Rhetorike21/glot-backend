package rhetorike.glot.domain._1auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MobileCertCode extends CertCode {
    private final String mobile;

    public MobileCertCode(String pinNumbers, String mobile) {
        super(pinNumbers);
        this.mobile = mobile;
    }
}
