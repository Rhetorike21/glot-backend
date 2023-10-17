package rhetorike.glot.domain._1auth.repository.resetcode;

import rhetorike.glot.domain._1auth.entity.ResetCode;

import java.util.Optional;

public interface ResetCodeRepository {
    ResetCode save(ResetCode resetCode);
    Optional<ResetCode> findByAccountId(String accountId);
    void deleteByAccountId(String accountId);
}
