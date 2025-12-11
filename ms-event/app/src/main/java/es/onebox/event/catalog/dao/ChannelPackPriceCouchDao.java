package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPricesDocument;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ChannelPackPriceCouchDao.CHANNEL_PACK_PRICES,
        bucket = ChannelPackPriceCouchDao.ONEBOX_OPERATIVE,
        scope = ChannelPackPriceCouchDao.CATALOG_SCOPE,
        collection = ChannelPackPriceCouchDao.CHANNEL_PACK_PRICE_COLLECTION)
public class ChannelPackPriceCouchDao extends AbstractCouchDao<ChannelPackPricesDocument> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String CATALOG_SCOPE = "catalog";
    public static final String CHANNEL_PACK_PRICE_COLLECTION = "channel-pack-price";
    public static final String CHANNEL_PACK_PRICES = "channelPackPrices";

    public ChannelPackPricesDocument get(Long channelId, Long packId) {
        return super.get(buildKey(channelId, packId));
    }

    private String buildKey(Long channelId, Long packId) {
        return channelId + "_" + packId;
    }
}
