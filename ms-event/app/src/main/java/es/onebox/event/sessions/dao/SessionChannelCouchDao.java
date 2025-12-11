package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
@CouchRepository(
        prefixKey = SessionChannelCouchDao.SESSION_CHANNEL,
        bucket = SessionChannelCouchDao.ONEBOX_OPERATIVE,
        scope = SessionChannelCouchDao.CATALOG_SCOPE,
        collection = SessionChannelCouchDao.COLLECTION)
public class SessionChannelCouchDao extends AbstractCouchDao<Serializable> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SESSION_CHANNEL = "sessionChannel";
    public static final String CATALOG_SCOPE = "catalog-rest";
    public static final String COLLECTION = "session-channels";

}

