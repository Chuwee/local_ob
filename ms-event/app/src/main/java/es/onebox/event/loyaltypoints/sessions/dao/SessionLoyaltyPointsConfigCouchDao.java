package es.onebox.event.loyaltypoints.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SessionLoyaltyPointsConfigCouchDao.LOYALTY_POINTS_CONFIG,
        bucket = SessionLoyaltyPointsConfigCouchDao.ONEBOX_OPERATIVE,
        scope = SessionLoyaltyPointsConfigCouchDao.SESSIONS_SCOPE,
        collection = SessionLoyaltyPointsConfigCouchDao.LOYALTY_POINTS_COLLECTION
)
public class SessionLoyaltyPointsConfigCouchDao extends AbstractCouchDao<SessionLoyaltyPointsConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SESSIONS_SCOPE = "sessions";
    public static final String LOYALTY_POINTS_COLLECTION = "loyalty-points";
    public static final String LOYALTY_POINTS_CONFIG = "loyaltyPointsConfig";

    public SessionLoyaltyPointsConfig getOrInitSessionLoyaltyPointsConfig(Long sessionId) {
        SessionLoyaltyPointsConfig sessionLoyaltyPointsConfig = super.get(sessionId.toString());
        return sessionLoyaltyPointsConfig == null ? new SessionLoyaltyPointsConfig() : sessionLoyaltyPointsConfig;
    }
}
