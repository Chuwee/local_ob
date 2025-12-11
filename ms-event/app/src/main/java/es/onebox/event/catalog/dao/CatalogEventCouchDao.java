package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = EventDataUtils.KEY_EVENT, bucket = CatalogEventCouchDao.BUCKET_ONEBOX_OPERATIVE, scope = "catalog", collection = "event")
public class CatalogEventCouchDao extends AbstractCouchDao<Event> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";

    public List<Event> bulkGet(List<Long> eventIds) {
        return bulkGet(getEventKeys(eventIds));
    }

    private static List<Key> getEventKeys(List<Long> eventIds) {
        return eventIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());
    }
}
