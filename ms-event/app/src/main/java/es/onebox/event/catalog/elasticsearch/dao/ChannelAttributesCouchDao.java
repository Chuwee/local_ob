package es.onebox.event.catalog.elasticsearch.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.attributes.ChannelAttributesDTO;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = ChannelAttributesCouchDao.CHANNEL_ATTRIBUTES_KEY, bucket = ChannelAttributesCouchDao.BUCKET_ONEBOX_OPERATIVE)
public class ChannelAttributesCouchDao extends AbstractCouchDao<ChannelAttributesDTO> {

    public static final String CHANNEL_ATTRIBUTES_KEY = "channelAttributes";
    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";

}
