package es.onebox.fifaqatar.config.config;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = FifaQatarConfigCouchDao.PREFIX_KEY,
        bucket = FifaQatarConfigCouchDao.BUCKET,
        scope = FifaQatarConfigCouchDao.SCOPE,
        collection = FifaQatarConfigCouchDao.COLLECTION)
public class FifaQatarConfigCouchDao extends AbstractCouchDao<FifaQatarConfigDocument> {

    public static final String PREFIX_KEY = "config";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "config";
}
