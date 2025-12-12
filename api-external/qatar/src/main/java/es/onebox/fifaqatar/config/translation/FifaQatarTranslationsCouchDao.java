package es.onebox.fifaqatar.config.translation;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = FifaQatarTranslationsCouchDao.PREFIX_KEY,
        bucket = FifaQatarTranslationsCouchDao.BUCKET,
        scope = FifaQatarTranslationsCouchDao.SCOPE,
        collection = FifaQatarTranslationsCouchDao.COLLECTION)
public class FifaQatarTranslationsCouchDao extends AbstractCouchDao<FifaQatarTranslation> {

    public static final String PREFIX_KEY = "translations";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "config";
}

