package rhetorike.glot.domain._1auth.repository;

import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.CertCode;

import java.util.Optional;

public interface CertCodeRepository {
    Optional<CertCode> findByPinNumbers(String pinNumbers);
    CertCode save(CertCode certCode);
    void update(CertCode certCode);
    void delete(CertCode certCode);
}
