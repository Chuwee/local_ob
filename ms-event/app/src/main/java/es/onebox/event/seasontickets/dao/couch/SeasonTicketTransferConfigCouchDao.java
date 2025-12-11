package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = SeasonTicketTransferConfigCouchDao.PREFIX,
        bucket = SeasonTicketTransferConfigCouchDao.BUCKET_NAME,
        scope = SeasonTicketTransferConfigCouchDao.SCOPE_NAME,
        collection = SeasonTicketTransferConfigCouchDao.COLLECTION_NAME)
public class SeasonTicketTransferConfigCouchDao extends AbstractCouchDao<SeasonTicketTransferConfig> {

    public static final String BUCKET_NAME = "onebox-operative";
    public static final String SCOPE_NAME = "season-tickets";
    public static final String COLLECTION_NAME = "transfers";
    public static final String PREFIX = "transfersConfig";

    public SeasonTicketTransferConfig getOrDefault(String seasonTicketId) {
        SeasonTicketTransferConfig config = this.get(seasonTicketId);

        if (config == null) {
            config = new SeasonTicketTransferConfig();
            config.setEnableBulk(false);
        }

        return config;
    }
}
