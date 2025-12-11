package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = EventDataUtils.KEY_SESSION, bucket = CatalogSessionCouchDao.BUCKET_ONEBOX_OPERATIVE, scope = "catalog", collection = "session")
public class CatalogSessionCouchDao extends AbstractCouchDao<Session> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";

    public List<Session> bulkGet(List<Long> sessionIds) {
        return bulkGet(getSessionKeys(sessionIds));
    }

    private static List<Key> getSessionKeys(List<Long> sessionIds) {
        return sessionIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());
    }
}
