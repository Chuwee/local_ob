package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketRenewalConfigCouchDao.PREFIX,
        bucket = SeasonTicketRenewalConfigCouchDao.BUCKET,
        scope = SeasonTicketRenewalConfigCouchDao.SCOPE,
        collection = SeasonTicketRenewalConfigCouchDao.COLLECTION
)
public class SeasonTicketRenewalConfigCouchDao extends AbstractCouchDao<SeasonTicketRenewalConfig> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "season-tickets";
    public static final String COLLECTION = "renewals";
    public static final String PREFIX = "renewalsConfig";
    
    public SeasonTicketRenewalConfig getOrInit(Long seasonTicketId) {
        SeasonTicketRenewalConfig renewalConfig = super.get(String.valueOf(seasonTicketId));
        if (renewalConfig == null) {
            renewalConfig = new SeasonTicketRenewalConfig();
            renewalConfig.setSeasonTicketId(seasonTicketId);
        }
        return renewalConfig;
    }
}
