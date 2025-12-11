package es.onebox.event.promotions.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = PromotionSessionCounterCouchDao.PROMOTION_SESSION_COUNTER, bucket = PromotionSessionCounterCouchDao.ONEBOX_OPERATIVE)
public class PromotionSessionCounterCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PROMOTION_SESSION_COUNTER = "promotionSessionCounter";

    public Long get(Integer promotionId, Integer sessionId) {
        String key = promotionId + "_" + sessionId;
        return super.get(key);
    }

}
