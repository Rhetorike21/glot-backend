package rhetorike.glot.domain._1auth.entity;

import lombok.Getter;

@Getter
public class EmailCertCode extends CertCode {
    private final String email;

    public EmailCertCode(String pinNumbers, String email) {
        super(pinNumbers);
        this.email = email;
    }
}
