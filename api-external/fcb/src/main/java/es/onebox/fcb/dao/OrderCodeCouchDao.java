package es.onebox.fcb.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.fcb.config.CouchbaseKeys;
import es.onebox.fcb.domain.OrderCode;
import org.springframework.stereotype.Repository;

/**
 * @author cgalindo
 */
@Repository
@CouchRepository(
        prefixKey = CouchbaseKeys.ORDER_KEY,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_INT,
        scope = CouchbaseKeys.SCOPE_FCB,
        collection = CouchbaseKeys.FCB_ORDERS_COLLECTION)
public class OrderCodeCouchDao extends AbstractCouchDao<OrderCode> {

}
