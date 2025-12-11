package es.onebox.event.attendants.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.attendants.domain.EventAttendantsConfig;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = EventAttendantConfigCouchDao.EVENT_ATTENDANT_CONFIG, bucket = EventAttendantConfigCouchDao.ONEBOX_OPERATIVE)
public class EventAttendantConfigCouchDao extends AbstractCouchDao<EventAttendantsConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EVENT_ATTENDANT_CONFIG = "eventAttendantConfig";

}
