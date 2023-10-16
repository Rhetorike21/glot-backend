package rhetorike.glot.domain._1auth.repository.blockedtoken;

import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._1auth.entity.BlockedToken;

import java.util.Optional;

@Repository
public class BlockedTokenRedisRepository implements BlockedTokenRepository {
    @Override
    public BlockedToken save(BlockedToken blockedToken) {
        return null;
    }

    @Override
    public Optional<BlockedToken> findByContent(String content) {
        return Optional.empty();
    }

    @Override
    public void delete(BlockedToken blockedToken) {

    }
}
