package es.onebox.fcb.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import es.onebox.fcb.config.CouchbaseKeys;
import org.springframework.stereotype.Repository;
import static es.onebox.fcb.config.CouchbaseKeys.B2C_PEOPLESOFT_COUNTER_KEY;

@Repository
@CouchRepository(
        prefixKey = B2C_PEOPLESOFT_COUNTER_KEY,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_INT,
        scope = CouchbaseKeys.SCOPE_FCB,
        collection = CouchbaseKeys.FCB_COUNTERS_COLLECTION)
public class B2CPeopleSoftCounterCouchDao extends AbstractCounterCouchDao<Long> {

    public Long getAndIncrement() {
        return autoIncrementCounter("");
    }
}