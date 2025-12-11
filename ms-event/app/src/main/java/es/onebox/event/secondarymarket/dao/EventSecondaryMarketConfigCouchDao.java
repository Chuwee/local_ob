package es.onebox.event.secondarymarket.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import org.springframework.stereotype.Repository;

/**
 * @author jgomez
 */

@Repository
@CouchRepository(prefixKey = EventSecondaryMarketConfigCouchDao.EVENT_SECONDARY_MARKET,
        bucket = EventSecondaryMarketConfigCouchDao.ONEBOX_OPERATIVE,
        scope = EventSecondaryMarketConfigCouchDao.EVENTS_SCOPE,
        collection = EventSecondaryMarketConfigCouchDao.SECONDARY_MARKET_COLLECTION)
public class EventSecondaryMarketConfigCouchDao extends AbstractCouchDao<EventSecondaryMarketConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EVENT_SECONDARY_MARKET = "secondaryMarket";
    public static final String EVENTS_SCOPE = "events";
    public static final String SECONDARY_MARKET_COLLECTION = "secondary-market";

}