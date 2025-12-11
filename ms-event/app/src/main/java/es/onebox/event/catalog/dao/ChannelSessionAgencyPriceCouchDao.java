package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelSessionAgencyPricesDocument;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import org.springframework.stereotype.Repository;


@Repository
@CouchRepository(prefixKey = ChannelSessionAgencyPriceCouchDao.CHANNEL_SESSION_PRICES,
        bucket = ChannelSessionAgencyPriceCouchDao.ONEBOX_OPERATIVE,
        scope = ChannelSessionAgencyPriceCouchDao.CATALOG_SCOPE,
        collection = ChannelSessionAgencyPriceCouchDao.CHANNEL_SESSION_PRICE_COLLECTION)
public class ChannelSessionAgencyPriceCouchDao extends AbstractCouchDao<ChannelSessionAgencyPricesDocument> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String CATALOG_SCOPE = "catalog";
    public static final String CHANNEL_SESSION_PRICE_COLLECTION = "channel-session-price";
    public static final String CHANNEL_SESSION_PRICES = "channelSessionAgencyPrices";


    public ChannelSessionPricesDocument get(Long channelId, Long sessionId, Long agencyId){
        return super.get(buildKey(channelId, sessionId, agencyId));
    }

    private String buildKey(Long channelId, Long sessionId, Long agencyId) {
        return channelId + "_" + sessionId + "_" + agencyId;
    }
}
