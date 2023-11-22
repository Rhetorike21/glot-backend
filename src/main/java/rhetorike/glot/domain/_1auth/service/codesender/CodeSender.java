package rhetorike.glot.domain._1auth.service.codesender;

import rhetorike.glot.domain._1auth.entity.CertCode;

public interface CodeSender<T extends CertCode> {
    void send(T certCode);
}
