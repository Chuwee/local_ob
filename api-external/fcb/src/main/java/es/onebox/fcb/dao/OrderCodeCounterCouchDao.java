package es.onebox.fcb.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import es.onebox.fcb.config.CouchbaseKeys;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = CouchbaseKeys.COUNTER_KEY,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_INT,
        scope = CouchbaseKeys.SCOPE_FCB,
        collection = CouchbaseKeys.FCB_COUNTERS_COLLECTION)
public class OrderCodeCounterCouchDao extends AbstractCounterCouchDao<Long> {

}
