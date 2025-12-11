package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SessionSecondaryMarketCustomersCounterCouchDao.KEY,
        bucket = SessionSecondaryMarketCustomersCounterCouchDao.BUCKET,
        scope = SessionSecondaryMarketCustomersCounterCouchDao.SCOPE,
        collection = SessionSecondaryMarketCustomersCounterCouchDao.COLLECTION
)
public class SessionSecondaryMarketCustomersCounterCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String KEY = "sessionSecondaryMarketCustomer";
    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "sessions";
    public static final String COLLECTION = "customer-counters";

    public Long get(Integer sessionId, String customerId) {
        return super.get(getKey(sessionId, customerId));
    }

    public Long autoIncrementCounter(Integer sessionId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoIncrementCounter(getKey(sessionId, customerId));
        } else {
            return super.incrementCounter(getKey(sessionId, customerId), amount.longValue());
        }
    }

    public Long autoDecrementCounter(Integer sessionId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoDecrementCounter(getKey(sessionId, customerId));
        } else {
            return super.decrementCounter(getKey(sessionId, customerId), amount.longValue());
        }
    }

    private String getKey(Integer sessionId, String customerId) {
        return sessionId + "_" + customerId;
    }
}
