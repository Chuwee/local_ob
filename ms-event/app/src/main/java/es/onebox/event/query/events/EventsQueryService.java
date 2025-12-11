package es.onebox.event.query.events;

import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.exception.MSEventNotFoundException;
import es.onebox.event.query.events.dto.EventQueryDTO;
import es.onebox.event.query.events.dto.converter.EventQueryDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventsQueryService {

    private final EventElasticDao eventElasticDao;

    @Autowired
    public EventsQueryService(EventElasticDao eventElasticDao) {
        this.eventElasticDao = eventElasticDao;
    }

    public EventQueryDTO getEvent(Long eventId) {
        EventData eventData = this.eventElasticDao.get(eventId);
        if (eventData == null || eventData.getEvent() == null) {
            throw new MSEventNotFoundException();
        }
        return EventQueryDTOConverter.from(eventData.getEvent());
    }
}
