package es.onebox.palisis.balance.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.palisis.balance.domain.OTABalance;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(
        prefixKey = EntityOTABalanceCouchDao.KEY,
        bucket = EntityOTABalanceCouchDao.BUCKET,
        scope = EntityOTABalanceCouchDao.SCOPE,
        collection = EntityOTABalanceCouchDao.COLLECTION)
public class EntityOTABalanceCouchDao extends AbstractCouchDao<OTABalance> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "entities";
    public static final String COLLECTION = "ota-balance";
    public static final String KEY = "entityBalance";

}
