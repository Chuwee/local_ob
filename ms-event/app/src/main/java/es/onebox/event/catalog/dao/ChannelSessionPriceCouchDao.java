package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


@Repository
@CouchRepository(prefixKey = ChannelSessionPriceCouchDao.CHANNEL_SESSION_PRICES,
        bucket = ChannelSessionPriceCouchDao.ONEBOX_OPERATIVE,
        scope = ChannelSessionPriceCouchDao.CATALOG_SCOPE,
        collection = ChannelSessionPriceCouchDao.CHANNEL_SESSION_PRICE_COLLECTION)
public class ChannelSessionPriceCouchDao extends AbstractCouchDao<ChannelSessionPricesDocument> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String CATALOG_SCOPE = "catalog";
    public static final String CHANNEL_SESSION_PRICE_COLLECTION = "channel-session-price";
    public static final String CHANNEL_SESSION_PRICES = "channelSessionPrices";


    public ChannelSessionPricesDocument get(Long channelId, Long sessionId) {
        return super.get(buildKey(channelId, sessionId));
    }

    private String buildKey(Long channelId, Long sessionId) {
        return channelId + "_" + sessionId;
    }

    public Map<Long, ChannelSessionPricesDocument> getBySessionIds(Long channelId, List<Integer> sessionIds) {
        return super.bulkGet(buildKeys(channelId, sessionIds)).stream().collect(toMap(ChannelSessionPricesDocument::getSessionId,
                Function.identity()));
    }

    private Collection<Key> buildKeys(Long channelId, List<Integer> sessionIds) {
       return sessionIds.stream().map(id -> {
            Key key = new Key();
            key.setKey(new String[]{channelId.toString(), id.toString()});
            return key;
        }).toList();
    }
}
