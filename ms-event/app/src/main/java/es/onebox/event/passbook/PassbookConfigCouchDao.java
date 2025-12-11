package es.onebox.event.passbook;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

/**
 * @author cgalindo
 */
@Repository
@CouchRepository(prefixKey = PassbookConfigCouchDao.PASSBOOK_CONFIG, bucket = PassbookConfigCouchDao.ONEBOX_OPERATIVE)
public class PassbookConfigCouchDao extends AbstractCouchDao<PassbookConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PASSBOOK_CONFIG = "passbookConfig";

}
