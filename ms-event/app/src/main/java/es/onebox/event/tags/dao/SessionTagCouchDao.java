package es.onebox.event.tags.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.tags.domain.SessionTagsCB;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = SessionTagCouchDao.KEY,
        bucket = SessionTagCouchDao.BUCKET_ONEBOX_OPERATIVE,
        scope = SessionTagCouchDao.SCOPE,
        collection = SessionTagCouchDao.COLLECTION)
public class SessionTagCouchDao extends AbstractCouchDao<SessionTagsCB> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
    public static final String KEY = "sessionTags";
    public static final String SCOPE = "sessions";
    public static final String COLLECTION = "tags";

    public SessionTagsCB getSessionTags(Long sessionId){
        return get(String.valueOf(sessionId));
    }
}
