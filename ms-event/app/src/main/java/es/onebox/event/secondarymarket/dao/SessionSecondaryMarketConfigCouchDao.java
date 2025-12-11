package es.onebox.event.secondarymarket.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketConfig;
import org.springframework.stereotype.Repository;

/**
 * @author jgomez
 */

@Repository
@CouchRepository(prefixKey = SessionSecondaryMarketConfigCouchDao.SESSION_SECONDARY_MARKET,
        bucket = SessionSecondaryMarketConfigCouchDao.ONEBOX_OPERATIVE,
        scope = SessionSecondaryMarketConfigCouchDao.SESSIONS_SCOPE,
        collection = SessionSecondaryMarketConfigCouchDao.SECONDARY_MARKET_COLLECTION)
public class SessionSecondaryMarketConfigCouchDao extends AbstractCouchDao<SessionSecondaryMarketConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SESSION_SECONDARY_MARKET = "secondaryMarket";
    public static final String SESSIONS_SCOPE = "sessions";
    public static final String SECONDARY_MARKET_COLLECTION = "secondary-market";

}