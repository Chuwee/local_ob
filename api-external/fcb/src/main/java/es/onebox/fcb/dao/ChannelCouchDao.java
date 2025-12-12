package es.onebox.fcb.dao;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.fcb.config.CouchbaseKeys;
import es.onebox.fcb.domain.Channel;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = CouchbaseKeys.CHANNEL_KEY,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_INT,
        scope = CouchbaseKeys.SCOPE_FCB,
        collection = CouchbaseKeys.CHANNEL_COLLECTION)
public class ChannelCouchDao extends AbstractCouchDao<Channel> {

    @Cached(key = "getChannelErpConfig", expires = 10 * 60)
    public Channel get(@CachedArg String channelId) {
        return super.get(channelId);
    }
}
