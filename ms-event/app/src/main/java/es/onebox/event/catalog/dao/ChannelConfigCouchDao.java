package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelConfigCB;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@CouchRepository(prefixKey = "channelConfig",
        bucket = "onebox-operative",
        scope = "channels",
        collection = "configs")
public class ChannelConfigCouchDao extends AbstractCouchDao<ChannelConfigCB> {


    public List<ChannelConfigCB> bulkGet(List<Long> channelIds) {
        List<Key> keys = channelIds.stream().map(channelId -> {
            Key key = new Key();
            key.setKey(new String[]{channelId.toString()});
            return key;
        }).toList();
        return super.bulkGet(keys);
    }

}
