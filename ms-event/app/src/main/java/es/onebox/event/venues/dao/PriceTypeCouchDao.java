package es.onebox.event.venues.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.couchbase.exception.CouchbaseException;
import es.onebox.event.venues.domain.PriceTypeConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

//Read only Dao
@Repository
@CouchRepository(prefixKey = PriceTypeCouchDao.PRICE_TYPE_CONFIG, bucket = PriceTypeCouchDao.ONEBOX_OPERATIVE)
public class PriceTypeCouchDao extends AbstractCouchDao<PriceTypeConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PRICE_TYPE_CONFIG = "priceTypeConfig";

    @Override
    public void insert(String id, PriceTypeConfig document) throws CouchbaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bulkInsert(List<PriceTypeConfig> documents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upsert(String id, PriceTypeConfig document) throws CouchbaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bulkUpsert(List<PriceTypeConfig> documents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String... ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bulkRemove(List<PriceTypeConfig> documents) {
        throw new UnsupportedOperationException();
    }
}
