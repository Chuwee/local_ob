package es.onebox.fifaqatar.conciliation.config;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = FifaQatarCustomerConciliationStopperDAO.DOC_NAME, bucket = FifaQatarCustomerConciliationStopperDAO.ONEBOX_OPERATIVE)
public class FifaQatarCustomerConciliationStopperDAO extends AbstractCounterCouchDao<Long> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String DOC_NAME = "fifaQatarCustomerConciliationStopper";

}