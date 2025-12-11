package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.domain.B2BSeatPublishingConfig;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = B2BPublishingConfigCouchDao.B2B_PUBLISHING_CONFIG,
        bucket = B2BPublishingConfigCouchDao.ONEBOX_OPERATIVE,
        scope = B2BPublishingConfigCouchDao.B2B_SCOPE,
        collection = B2BPublishingConfigCouchDao.PUBLISHING_CONFIG_COLLECTION)
public class B2BPublishingConfigCouchDao extends AbstractCouchDao<B2BSeatPublishingConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String B2B_PUBLISHING_CONFIG = "b2bPublishingConfig";
    public static final String B2B_SCOPE = "b2b";
    public static final String PUBLISHING_CONFIG_COLLECTION = "publishing-configs";

}
