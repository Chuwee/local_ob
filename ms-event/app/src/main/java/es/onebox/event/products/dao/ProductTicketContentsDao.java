package es.onebox.event.products.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.products.dao.couch.ProductTicketContent;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@CouchRepository(prefixKey = ProductTicketContentsDao.PREFIX,
        bucket = ProductTicketContentsDao.BUCKET,
        scope = ProductTicketContentsDao.SCOPE,
        collection = ProductTicketContentsDao.COLLECTION)
public class ProductTicketContentsDao extends AbstractCouchDao<ProductTicketContent> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "ticket-contents";
    public static final String PREFIX = "productTicketContents";

    public ProductTicketContent getOrCreate(Long productId) {
        ProductTicketContent productTicketContent = this.get(productId.toString());
        return Objects.requireNonNullElseGet(productTicketContent, () -> new ProductTicketContent(productId));
    }
}
