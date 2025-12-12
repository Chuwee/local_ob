package es.onebox.fifaqatar.adapter.mapping;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = TicketDetailMappingCouchDao.PREFIX_KEY,
        bucket = TicketDetailMappingCouchDao.BUCKET,
        scope = TicketDetailMappingCouchDao.SCOPE,
        collection = TicketDetailMappingCouchDao.COLLECTION)
public class TicketDetailMappingCouchDao extends AbstractCouchDao<TicketDetailMapping> {

    public static final String PREFIX_KEY = "ticket";
    public static final String BUCKET = "onebox-int";
    public static final String SCOPE = "fifa-qatar";
    public static final String COLLECTION = "ticket-mapping";
}
