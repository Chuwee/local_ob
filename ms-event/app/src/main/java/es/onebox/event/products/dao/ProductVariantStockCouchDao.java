package es.onebox.event.products.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CouchRepository(prefixKey = ProductVariantStockCouchDao.PREFIX,
        bucket = ProductVariantStockCouchDao.BUCKET,
        scope = ProductVariantStockCouchDao.SCOPE,
        collection = ProductVariantStockCouchDao.COLLECTION)
public class ProductVariantStockCouchDao extends AbstractCounterCouchDao<Long> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "variants-stock";
    public static final String PREFIX = "productVariantStock";

    public void insert(Long productId, Long variantId) {
        super.createCounter(concatKeys(productId, variantId), 0L);
    }

    public void updateStock(Long productId, Long variantId, Long counter) {
        super.resetCounter(concatKeys(productId, variantId), counter);
    }

    public Long get(Long productId, Long variantId) {
        return super.get(concatKeys(productId, variantId));
    }

    public boolean exists(Long productId, Long variantId) {
        return super.exists(concatKeys(productId, variantId));
    }

    public Long decrement(Long productId, Long variantId, Long quantity) {
        return super.decrementCounter(concatKeys(productId, variantId), quantity);
    }

    public Long increment(Long productId, Long variantId, Long quantity) {
        return super.incrementCounter(concatKeys(productId, variantId), quantity);
    }

    public List<Long> bulkGet(Long productId, List<Long> variantIds) {
        return bulkGet(getKeys(productId, variantIds));
    }

    private String concatKeys(Long productId, Long variantId) {
        return productId + "_" + variantId;
    }

    private List<Key> getKeys(Long productId, List<Long> variantIds) {
        return variantIds.stream()
                .map(variantId -> {
                    Key key = new Key();
                    key.setKey(new String[]{concatKeys(productId, variantId)});
                    return key;
                })
                .toList();
    }
}
