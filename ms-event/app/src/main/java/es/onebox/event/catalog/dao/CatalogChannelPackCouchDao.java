package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.config.CouchbaseKeys;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CatalogChannelPackCouchDao.CHANNEL_PACK,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_OPERATIVE,
        scope = CatalogChannelPackCouchDao.CATALOG_SCOPE,
        collection = CatalogChannelPackCouchDao.PACK_COLLECTION)
public class CatalogChannelPackCouchDao extends AbstractCouchDao<ChannelPack> {

    final static String CHANNEL_PACK = "channelPack";
    final static String CATALOG_SCOPE = "catalog";
    final static String PACK_COLLECTION = "pack";

    public void upsert(ChannelPack channelPack) {
        upsert(getKey(channelPack), channelPack);
    }

    public ChannelPack get(Long channelId, Long packId) {
        return super.get(getKey(channelId, packId));
    }

    private String getKey(ChannelPack channelPack) {
        return getKey(channelPack.getChannelId(), channelPack.getId());
    }

    private String getKey(Long channelId, Long packId) {
        return channelId + "_" + packId;
    }
}
