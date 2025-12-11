package es.onebox.event.attendants.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = SessionAttendantConfigCouchDao.ATTENDANT_CONFIG, bucket = SessionAttendantConfigCouchDao.ONEBOX_OPERATIVE)
public class SessionAttendantConfigCouchDao extends AbstractCouchDao<SessionAttendantsConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String ATTENDANT_CONFIG = "attendantConfig";

}
