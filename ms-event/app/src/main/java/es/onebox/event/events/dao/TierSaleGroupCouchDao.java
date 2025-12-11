package es.onebox.event.events.dao;


import es.onebox.couchbase.CouchCounter;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = TierSaleGroupCouchDao.PREFIX, bucket = TierSaleGroupCouchDao.BUCKET)
public class TierSaleGroupCouchDao extends AbstractCounterCouchDao<CouchCounter> {

    public static final String BUCKET = "onebox-operative";
    public static final String PREFIX = "tierSaleGroup";

    public void insert(Long tierId, Long saleGroupId, Integer counter) {
        createCounter(buildId(tierId, saleGroupId), counter.longValue());
    }

    public void updateCounter(Long tierId, Long saleGroupId, Integer counter) {
        resetCounter(buildId(tierId, saleGroupId), counter.longValue());
    }

    public long decrement(Long tierId, Long saleGroupId){
        return super.decrementCounter(buildId(tierId, saleGroupId), 1L);
    }

    public long increment(Long tierId, Long saleGroupId){
        return super.incrementCounter(buildId(tierId, saleGroupId), 1L);
    }

    public long get(Long tierId, Long saleGroupId){
        return super.get(buildId(tierId, saleGroupId));
    }


    private String buildId(Long tierId, Long saleGroupId) {
        return tierId + "_" + saleGroupId;
    }

}
