package es.onebox.event.products.dao;


import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ProductCommunicationElementCouchDao.PREFIX,
        bucket = ProductCommunicationElementCouchDao.BUCKET,
        scope = ProductCommunicationElementCouchDao.SCOPE,
        collection = ProductCommunicationElementCouchDao.COLLECTION)
public class ProductCommunicationElementCouchDao extends AbstractCouchDao<ProductCommunicationElementDocument> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "communication-element";
    public static final String PREFIX = "productCommunicationElement";

}
