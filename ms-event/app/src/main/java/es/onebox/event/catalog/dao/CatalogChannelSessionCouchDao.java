package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = EventDataUtils.KEY_CHANNEL_SESSION, bucket = CatalogChannelSessionCouchDao.BUCKET_ONEBOX_OPERATIVE, scope = "catalog", collection = "channel-session")
public class CatalogChannelSessionCouchDao extends AbstractCouchDao<ChannelSession> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
}
