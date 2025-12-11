package es.onebox.event.products.dao;


import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ProductCatalogCouchDao.PREFIX,
        bucket = ProductCatalogCouchDao.BUCKET,
        scope = ProductCatalogCouchDao.SCOPE,
        collection = ProductCatalogCouchDao.COLLECTION)
public class ProductCatalogCouchDao extends AbstractCouchDao<ProductCatalogDocument> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "product-catalog";
    public static final String PREFIX = "productCatalog";

}
