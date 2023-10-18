package rhetorike.glot.domain._1auth.service.smscert.smssender;

import rhetorike.glot.domain._1auth.entity.CertCode;

public interface SmsSender {
    void sendCertCode(String mobile, CertCode certCode);
}
