package es.onebox.fifaqatar.adapter.mapping;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = CustomerMappingCouchDao.PREFIX_KEY,
        bucket = CustomerMappingCouchDao.BUCKET,
        scope = CustomerMappingCouchDao.SCOPE,
        collection = CustomerMappingCouchDao.COLLECTION)
public class CustomerMappingCouchDao extends AbstractCouchDao<String> {

    public static final String PREFIX_KEY = "order";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "customer-mapping";
}
