package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.sessions.domain.ExternalSessionConfig;
import org.springframework.stereotype.Repository;

/**
 * @author rfontecha
 */
@Repository
@CouchRepository(prefixKey = ExternalSessionConfigConfigCouchDao.EXTERNAL_SESSION_CONFIG, bucket = ExternalSessionConfigConfigCouchDao.ONEBOX_OPERATIVE)
public class ExternalSessionConfigConfigCouchDao extends AbstractCouchDao<ExternalSessionConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EXTERNAL_SESSION_CONFIG = "externalSessionConfig";

}
