package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.seasontickets.dto.redemption.SeasonTicketRedemption;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketRedemptionConfigCouchDao.PREFIX,
        bucket = SeasonTicketRedemptionConfigCouchDao.BUCKET_NAME,
        scope = SeasonTicketRedemptionConfigCouchDao.SCOPE_NAME,
        collection = SeasonTicketRedemptionConfigCouchDao.COLLECTION_NAME)
public class SeasonTicketRedemptionConfigCouchDao extends AbstractCouchDao<SeasonTicketRedemption> {

    public static final String BUCKET_NAME = "onebox-operative";
    public static final String SCOPE_NAME = "season-tickets";
    public static final String COLLECTION_NAME = "ticket-redemption";
    public static final String PREFIX = "redemptionConfig";

    public SeasonTicketRedemption getOrDefault(String seasonTicketId) {
        SeasonTicketRedemption config = this.get(seasonTicketId);

        if (config == null) {
            config = new SeasonTicketRedemption();
            config.setEnabled(false);
        }

        return config;
    }
}
