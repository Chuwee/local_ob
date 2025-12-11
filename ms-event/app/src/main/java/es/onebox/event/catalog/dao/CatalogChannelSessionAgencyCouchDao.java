package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = EventDataUtils.KEY_CHANNEL_SESSION_AGENCY, bucket = CatalogChannelSessionAgencyCouchDao.BUCKET_ONEBOX_OPERATIVE, scope = "catalog", collection = "channel-session")
public class CatalogChannelSessionAgencyCouchDao extends AbstractCouchDao<ChannelSessionAgency> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
}
