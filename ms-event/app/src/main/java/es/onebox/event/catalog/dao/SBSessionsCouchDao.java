package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = SBSessionsCouchDao.PREFIX, bucket = SBSessionsCouchDao.ONEBOX_INT,
        scope = SBSessionsCouchDao.FCB_SCOPE, collection = SBSessionsCouchDao.SESSIONS_COLLECTION)
public class SBSessionsCouchDao extends AbstractCouchDao<SBSession> {

    public static final String ONEBOX_INT = "onebox-int";
    public static final String PREFIX = "sbSession";
    public static final String FCB_SCOPE = "fcb";
    public static final String SESSIONS_COLLECTION = "sessions";

    public List<SBSession> bulkGet(Set<Long> sessionIds) {
        return bulkGet(getSbSessionKeys(sessionIds));
    }

    private static List<Key> getSbSessionKeys(Set<Long> sessionIds) {
        return sessionIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());
    }


}
