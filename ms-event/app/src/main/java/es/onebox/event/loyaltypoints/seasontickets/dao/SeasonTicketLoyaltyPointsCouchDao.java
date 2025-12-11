package es.onebox.event.loyaltypoints.seasontickets.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.loyaltypoints.seasontickets.domain.SeasonTicketLoyaltyPointsConfig;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketLoyaltyPointsCouchDao.PREFIX,
        bucket = SeasonTicketLoyaltyPointsCouchDao.BUCKET,
        scope = SeasonTicketLoyaltyPointsCouchDao.SCOPE,
        collection = SeasonTicketLoyaltyPointsCouchDao.COLLECTION
)
public class SeasonTicketLoyaltyPointsCouchDao extends AbstractCouchDao<SeasonTicketLoyaltyPointsConfig> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "season-tickets";
    public static final String COLLECTION = "loyalty-points";
    public static final String PREFIX = "loyaltyPoints";
}