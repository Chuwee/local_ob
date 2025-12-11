package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitConfig;
import org.springframework.stereotype.Repository;

@Repository
public class UserRateLimitRepository {

    private final MsEntityDatasource msEntityDatasource;


    public UserRateLimitRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public UserRateLimitConfig searchRateLimit(Long userId) {
        return msEntityDatasource.searchRateLimit(userId);
    }

    public void upsertRateLimit(Long userId, UserRateLimitConfig userRateLimitConfig) {
        msEntityDatasource.upsertRateLimit(userId, userRateLimitConfig);
    }

}
