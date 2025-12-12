package es.onebox.atm.categories.dao;

import es.onebox.atm.categories.dto.AtmCategory;
import es.onebox.cache.annotation.Cached;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@CouchRepository(prefixKey = AtmCategoriesDao.PREFIX, bucket = AtmCategoriesDao.ONEBOX_OPERATIVE)
public class AtmCategoriesDao extends AbstractCouchDao<AtmCategory> {
    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PREFIX = "atmCategories";

    @Cached(key = "getAtmCategories", expires = 10, timeUnit = TimeUnit.MINUTES)
    public AtmCategory get() {
        return super.get("");
    }
}
