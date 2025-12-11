package es.onebox.event.promotions.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = PromotionEventCounterCouchDao.PROMOTION_EVENT_COUNTER, bucket = PromotionEventCounterCouchDao.ONEBOX_OPERATIVE)
public class PromotionEventCounterCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PROMOTION_EVENT_COUNTER = "promotionEventCounter";
    public Long get(Integer promotionId, Integer eventId) {
        String key = promotionId + "_" + eventId;
        return super.get(key);
    }
}
