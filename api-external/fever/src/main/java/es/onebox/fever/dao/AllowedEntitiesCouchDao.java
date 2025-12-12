package es.onebox.fever.dao;

import es.onebox.common.datasources.webhook.dto.fever.AllowedEntitiesFileData;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = AllowedEntitiesCouchDao.PREFIX,
        bucket = AllowedEntitiesCouchDao.ONEBOX_OPERATIVE,
        scope = AllowedEntitiesCouchDao.SCOPE,
        collection = AllowedEntitiesCouchDao.COLLECTION)
public class AllowedEntitiesCouchDao extends AbstractCouchDao<AllowedEntitiesFileData> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SCOPE = "webhooks";

    public static final String COLLECTION = "allowed-entities";
    public static final String PREFIX = "allowedEntities";

}
