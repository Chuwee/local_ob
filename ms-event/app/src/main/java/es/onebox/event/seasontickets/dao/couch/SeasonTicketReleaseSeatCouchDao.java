package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketReleaseSeatCouchDao.PREFIX,
        bucket = SeasonTicketReleaseSeatCouchDao.BUCKET,
        scope = SeasonTicketReleaseSeatCouchDao.SCOPE,
        collection = SeasonTicketReleaseSeatCouchDao.COLLECTION
)
public class SeasonTicketReleaseSeatCouchDao extends AbstractCouchDao<SeasonTicketReleaseSeat> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "season-tickets";
    public static final String COLLECTION = "release-seat";
    public static final String PREFIX = "releaseSeat";
}
