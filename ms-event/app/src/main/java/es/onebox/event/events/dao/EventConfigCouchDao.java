package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@CouchRepository(
        prefixKey = EventConfigCouchDao.EVENT_CONFIG,
        bucket = EventConfigCouchDao.ONEBOX_OPERATIVE,
        scope = EventConfigCouchDao.EVENTS_SCOPE,
        collection = EventConfigCouchDao.CONFIG_COLLECTION
)
public class EventConfigCouchDao extends AbstractCouchDao<EventConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EVENT_CONFIG = "eventConfig";
    public static final String EVENTS_SCOPE = "events";
    public static final String CONFIG_COLLECTION = "configs";

    public EventConfig getOrInitEventConfig(Long eventId) {
        EventConfig eventConfig = super.get(eventId.toString());
        if (eventConfig == null) {
            eventConfig = new EventConfig();
            eventConfig.setEventId(eventId.intValue());
        }
        return eventConfig;
    }

    public List<EventConfig> bulkGet(List<Long> eventIds) {
        List<Key> ids = getEventConfigKeys(eventIds);
        return bulkGet(ids);
    }

    private static List<Key> getEventConfigKeys(List<Long> eventIds) {
        return eventIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());
    }
}
