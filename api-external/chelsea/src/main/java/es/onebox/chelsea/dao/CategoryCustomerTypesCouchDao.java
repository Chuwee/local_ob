package es.onebox.chelsea.dao;

import es.onebox.chelsea.domain.CategoryCustomerTypesMapping;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CategoryCustomerTypesCouchDao.PREFIX,
        bucket = CategoryCustomerTypesCouchDao.ONEBOX_OPERATIVE,
        scope = CategoryCustomerTypesCouchDao.SCOPE,
        collection = CategoryCustomerTypesCouchDao.COLLECTION)
public class CategoryCustomerTypesCouchDao extends AbstractCouchDao<CategoryCustomerTypesMapping> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SCOPE = "entities";

    public static final String COLLECTION = "category-customer-mappings";
    public static final String PREFIX = "categoryMapping";

}
