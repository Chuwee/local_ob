package es.onebox.event.products.dao;


import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ProductAttributeContentsCouchDao.PREFIX,
        bucket = ProductAttributeContentsCouchDao.BUCKET,
        scope = ProductAttributeContentsCouchDao.SCOPE,
        collection = ProductAttributeContentsCouchDao.COLLECTION)
public class ProductAttributeContentsCouchDao extends AbstractCouchDao<ProductContentDocument> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "texts";
    public static final String PREFIX = "productAttributeCommunicationElement";

    public ProductContentDocument get(Long productId, Long attributeId) {
        String key = getKey(productId, attributeId);
        ProductContentDocument doc = super.get(key);
        // TODO: temporal fix, delete me afterwards
        if (doc != null) {
            if (doc.getProductId() == null) {
                doc.setProductId(productId);
            }
            if (doc.getAttributeId() == null) {
                doc.setAttributeId(attributeId);
            }
        }
        return doc;
    }

    public String getKey(Long productId, Long attributeId) {
        return productId + "_" + attributeId;
    }

}
