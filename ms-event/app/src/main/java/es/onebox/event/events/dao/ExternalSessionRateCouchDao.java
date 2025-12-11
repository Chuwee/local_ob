package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.domain.ExternalRateType;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ExternalSessionRateCouchDao.EXTERNAL_RATE_TYPES,
        bucket = ExternalSessionRateCouchDao.ONEBOX_INT,
        scope = ExternalSessionRateCouchDao.ITALY_COMPLIANCE,
        collection = ExternalSessionRateCouchDao.RATES_ITH_MAPPINGS)
public class ExternalSessionRateCouchDao extends AbstractCouchDao<ExternalRateType> {

    public static final String ONEBOX_INT = "onebox-int";
    public static final String EXTERNAL_RATE_TYPES = "externalRateTypes";
    public static final String ITALY_COMPLIANCE = "italy-compliance";
    public static final String RATES_ITH_MAPPINGS = "rates-ith-mappings";

}
