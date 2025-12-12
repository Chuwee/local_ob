package es.onebox.fifaqatar.adapter.gif;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = GifRepositoryCouchDao.KEY,
        bucket = GifRepositoryCouchDao.BUCKET,
        scope = GifRepositoryCouchDao.SCOPE,
        collection = GifRepositoryCouchDao.COLLECTION)
public class GifRepositoryCouchDao extends AbstractCouchDao<String> {

    public static final String KEY = "barcode";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "barcode";
}
