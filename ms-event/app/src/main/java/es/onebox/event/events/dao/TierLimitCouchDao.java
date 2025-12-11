package es.onebox.event.events.dao;


import es.onebox.couchbase.CouchCounter;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = TierLimitCouchDao.PREFIX, bucket = TierLimitCouchDao.BUCKET)
public class TierLimitCouchDao extends AbstractCounterCouchDao<CouchCounter> {

    public static final String BUCKET = "onebox-operative";
    public static final String PREFIX = "tierLimit";

    public void insert(Long tierId, Long counter) {
        super.createCounter(tierId.toString(), counter);
    }

    public void updateCounter(Long tierId, Long counter) {
        super.resetCounter(tierId.toString(), counter);
    }

    public Long get(Long tierId) {
        return super.get(tierId.toString());
    }

    public boolean exists(Long tierId) {
        return super.exists(tierId.toString());
    }

    public void remove(Long tierId){
        super.remove(tierId.toString());
    }

    public Long decrement(Long tierId){
        return super.decrementCounter(tierId.toString(), 1L);
    }

    public Long increment(Long tierId){
        return super.incrementCounter(tierId.toString(), 1L);
    }
}
