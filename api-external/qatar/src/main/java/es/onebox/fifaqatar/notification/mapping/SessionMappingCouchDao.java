package es.onebox.fifaqatar.notification.mapping;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodesMapping;
import org.springframework.stereotype.Repository;


@Repository
@CouchRepository(
        prefixKey = SessionMappingCouchDao.PREFIX_KEY,
        bucket = SessionMappingCouchDao.BUCKET,
        scope = SessionMappingCouchDao.SCOPE,
        collection = SessionMappingCouchDao.COLLECTION)
public class SessionMappingCouchDao extends AbstractCouchDao<SessionBarcodesMapping> {

    public static final String PREFIX_KEY = "sessionBarcodesMapping";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "config";
}

