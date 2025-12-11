package es.onebox.event.events.dynamicpricing;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = ActiveDynamicPricingDao.PREFIX,
        bucket = ActiveDynamicPricingDao.BUCKET,
        scope = ActiveDynamicPricingDao.SCOPE,
        collection = ActiveDynamicPricingDao.COLLECTION
)
public class ActiveDynamicPricingDao  extends AbstractCouchDao<EventDynamicPricing> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "events";
    public static final String COLLECTION = "dynamic-pricing";
    public static final String PREFIX = "eventDynamicPricing";
}
