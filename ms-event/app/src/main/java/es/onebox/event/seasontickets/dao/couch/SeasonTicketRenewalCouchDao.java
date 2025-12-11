package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketRenewalCouchDao.PREFIX,
        bucket = SeasonTicketRenewalCouchDao.BUCKET,
        scope = SeasonTicketRenewalCouchDao.SCOPE,
        collection = SeasonTicketRenewalCouchDao.COLLECTION
)
public class SeasonTicketRenewalCouchDao extends AbstractCouchDao<SeasonTicketRenewalCouchDocument> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "customers";
    public static final String COLLECTION = "renewals";
    public static final String PREFIX = "renewals";
}
