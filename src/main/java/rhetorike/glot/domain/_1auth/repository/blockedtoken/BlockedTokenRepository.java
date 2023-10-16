package rhetorike.glot.domain._1auth.repository.blockedtoken;

import rhetorike.glot.domain._1auth.entity.BlockedToken;

import java.util.Optional;

public interface BlockedTokenRepository {
    BlockedToken save(BlockedToken blockedToken);

    Optional<BlockedToken> findByContent(String content);

    void delete(BlockedToken blockedToken);

}
