package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SessionCustomersCounterCouchDao.KEY,
        bucket = SessionCustomersCounterCouchDao.BUCKET,
        scope = SessionCustomersCounterCouchDao.SCOPE,
        collection = SessionCustomersCounterCouchDao.COLLECTION
)
public class SessionCustomersCounterCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String KEY = "sessionCustomer";
    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "sessions";
    public static final String COLLECTION = "customer-counters";

    public Long get(Integer sessionId, Integer priceTypeId, String customerId) {
        return super.get(getKey(sessionId, priceTypeId, customerId));
    }

    public Long autoIncrementSCPriceTypeCounter(Integer sessionId, Integer priceTypeId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoIncrementCounter(getKey(sessionId, priceTypeId, customerId));
        } else {
            return super.incrementCounter(getKey(sessionId, priceTypeId, customerId), amount.longValue());
        }
    }

    public Long autoIncrementSCCounter(Integer sessionId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoIncrementCounter(getKey(sessionId, customerId));
        } else {
            return super.incrementCounter(getKey(sessionId, customerId), amount.longValue());
        }
    }

    public Long autoDecrementSCPriceTypeCounter(Integer sessionId, Integer priceTypeId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoDecrementCounter(getKey(sessionId, priceTypeId, customerId));
        } else {
            return super.decrementCounter(getKey(sessionId, priceTypeId, customerId), amount.longValue());
        }
    }

    public Long autoDecrementSCCounter(Integer sessionId, String customerId, Integer amount) {
        if (amount == null) {
            return super.autoDecrementCounter(getKey(sessionId, customerId));
        } else {
            return super.decrementCounter(getKey(sessionId, customerId), amount.longValue());
        }
    }

    private String getKey(Integer sessionId, Integer priceTypeId, String customerId) {
        return sessionId + "_" + priceTypeId + "_" + customerId;
    }

    private String getKey(Integer sessionId, String customerId) {
        return sessionId + "_" + customerId;
    }
}
