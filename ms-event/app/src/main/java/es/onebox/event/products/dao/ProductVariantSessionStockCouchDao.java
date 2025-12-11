package es.onebox.event.products.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CouchRepository(prefixKey = ProductVariantSessionStockCouchDao.PREFIX,
        bucket = ProductVariantSessionStockCouchDao.BUCKET,
        scope = ProductVariantSessionStockCouchDao.SCOPE,
        collection = ProductVariantSessionStockCouchDao.COLLECTION)
public class ProductVariantSessionStockCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "variants-sessions-stock";
    public static final String PREFIX = "productVariantSessionStock";

    public void createAndInit(Long productId, Long variantId, Long sessionId, Long initialValue) {
        super.createCounter(concatKeys(productId, variantId, sessionId), initialValue);
    }

    public Long get(Long productId, Long variantId, Long sessionId) {
        return super.get(concatKeys(productId, variantId, sessionId));
    }

    public boolean exists(Long productId, Long variantId, Long sessionId) {
        return super.exists(concatKeys(productId, variantId, sessionId));
    }

    public Long decrement(Long productId, Long variantId, Long sessionId, Long quantity) {
        return super.decrementCounter(concatKeys(productId, variantId, sessionId), quantity);
    }

    public Long increment(Long productId, Long variantId, Long sessionId, Long quantity) {
        return super.incrementCounter(concatKeys(productId, variantId, sessionId), quantity);
    }

    public static String concatKeys(Long productId, Long variantId, Long sessionId) {
        return productId + "_" + variantId + "_" + sessionId;
    }

    public void resetIfExists(Long productId, Long variantId, Long sessionId, Long initialValue) {
        if (exists(productId, variantId, sessionId)) {
            resetCounter(concatKeys(productId, variantId, sessionId), initialValue);
        }
    }

}
