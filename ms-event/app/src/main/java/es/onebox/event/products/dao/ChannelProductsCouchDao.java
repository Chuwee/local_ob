package es.onebox.event.products.dao;


import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.products.dao.couch.ChannelProductDocument;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ChannelProductsCouchDao.PREFIX,
        bucket = ChannelProductsCouchDao.BUCKET,
        scope = ChannelProductsCouchDao.SCOPE,
        collection = ChannelProductsCouchDao.COLLECTION)
public class ChannelProductsCouchDao extends AbstractCouchDao<ChannelProductDocument> {

    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "products";
    public static final String COLLECTION = "channel-products";
    public static final String PREFIX = "channelProducts";

}
