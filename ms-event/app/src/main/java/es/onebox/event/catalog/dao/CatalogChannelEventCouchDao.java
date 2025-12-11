package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = EventDataUtils.KEY_CHANNEL_EVENT, bucket = CatalogChannelEventCouchDao.BUCKET_ONEBOX_OPERATIVE, scope = "catalog", collection = "channel-event")
public class CatalogChannelEventCouchDao extends AbstractCouchDao<ChannelEvent> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
}
