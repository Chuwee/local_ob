package es.onebox.event.products.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.config.CouchbaseKeys;
import es.onebox.event.products.dao.couch.ProductTicketLiterals;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CouchbaseKeys.PRODUCT_TICKET_LITERALS_PREFIX,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_OPERATIVE,
        scope = CouchbaseKeys.PRODUCTS,
        collection = CouchbaseKeys.PRODUCT_TICKET_LITERALS_COLLECTION)
public class ProductTicketLiteralsCouchDao extends AbstractCouchDao<ProductTicketLiterals> {

    public void upsert(Integer templateId, String lang, ProductTicketLiterals literals) {
        String key = buildKey(templateId, lang);
        upsert(key, literals);
    }

    private String buildKey(Integer templateId, String lang) {
        return templateId + "_" + lang;
    }
}
