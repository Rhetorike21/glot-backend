package rhetorike.glot.domain._1auth.repository.blockedtoken;

import rhetorike.glot.domain._1auth.entity.BlockedToken;
import rhetorike.glot.global.security.jwt.ServiceToken;

import java.util.Optional;

public interface BlockedTokenRepository {
    void save(ServiceToken serviceToken);

    boolean doesExist(String jwt);

    void delete(ServiceToken blockedToken);

}
