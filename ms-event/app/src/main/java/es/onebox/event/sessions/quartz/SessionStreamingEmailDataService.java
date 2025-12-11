package es.onebox.event.sessions.quartz;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionStreamingEmailDataService {

    @Autowired
    private SessionConfigCouchDao sessionConfigCouchDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private EventCommunicationElementDao eventCommunicationElementDao;

    @Autowired
    private StaticDataContainer staticDataContainer;

    @Autowired
    private EntitiesRepository entitiesRepository;

    private SessionConfig sessionConfig;
    private EntityDTO entity;
    private CpanelCanalRecord cpanelCanalRecord;
    private String eventName;
    private String sessionName;
    private String sessionTimezone;
    private String eventImageUrl;

    public SessionConfig getSessionConfigCouch(Long sessionId) {
        if (sessionConfig == null) {
            sessionConfig = sessionConfigCouchDao.get(sessionId.toString());
        }
        return sessionConfig;
    }

    public EntityDTO getEntity(Integer entityId) {
        if (entity == null) {
            entity = entitiesRepository.getEntity(entityId);
        }
        return entity;
    }

    public CpanelCanalRecord getChannel(Integer channelId) {
        if (cpanelCanalRecord == null) {
            cpanelCanalRecord = channelDao.getById(channelId);
        }
        return cpanelCanalRecord;
    }


    public String getEventName(String language, Long eventId, String defaultEventName) {
        if (eventName == null) {
            EventCommunicationElementFilter filter = new EventCommunicationElementFilter();
            filter.setTags(new HashSet<>(Arrays.asList(EventTagType.TEXT_TITLE_WEB)));
            filter.setLanguageId(this.staticDataContainer.getLanguageByCode(language));
            List<CpanelElementosComEventoRecord> eventCommunicationElements = eventCommunicationElementDao
                    .findCommunicationElements(eventId, null, null, filter);

            eventName = eventCommunicationElements.stream().findFirst().map(CpanelElementosComEventoRecord::getValor).orElse(defaultEventName);
        }
        return eventName;
    }

    public String getSessionName(String language, Long sessionId) {
        if (sessionName == null) {
            EventCommunicationElementFilter filter = new EventCommunicationElementFilter();
            filter.setTags(new HashSet<>(Arrays.asList(EventTagType.TEXT_TITLE_WEB)));
            filter.setLanguageId(this.staticDataContainer.getLanguageByCode(language));
            List<CpanelElementosComEventoRecord> eventCommunicationElements = eventCommunicationElementDao
                    .findCommunicationElements(null, Collections.singleton(sessionId.intValue()), null, filter);

            SessionRecord sessionRecord = sessionDao.findSession(sessionId);

            sessionName = eventCommunicationElements.stream().findFirst().map(CpanelElementosComEventoRecord::getValor).orElse(sessionRecord.getNombre());
            sessionTimezone = sessionRecord.getVenueTZ();
        }
        return sessionName;
    }

    public String getSessionVenueTZ(Long sessionId) {
        if (sessionTimezone == null) {
            SessionRecord sessionRecord = sessionDao.findSession(sessionId);
            sessionTimezone = sessionRecord.getVenueTZ();
        }
        return sessionTimezone;
    }

    public String getEventImageUrl(String language, Long eventId, Long sessionId) {
        if (eventImageUrl == null) {
            EventCommunicationElementFilter filter = new EventCommunicationElementFilter();
            filter.setTags(new HashSet<>(Arrays.asList(EventTagType.IMG_BANNER_WEB)));
            filter.setLanguageId(this.staticDataContainer.getLanguageByCode(language));
            List<CpanelElementosComEventoRecord> eventCommunicationElements = eventCommunicationElementDao
                    .findCommunicationElements(eventId, null, null, filter);

            SessionRecord sessionRecord = sessionDao.findSession(sessionId);

            String image = eventCommunicationElements.stream()
                    .sorted(Comparator.comparingInt(CpanelElementosComEventoRecord::getPosition))
                    .map(CpanelElementosComEventoRecord::getValor)
                    .findFirst().orElse(null);
            if (image != null) {
                eventImageUrl = S3URLResolver.builder()
                        .withUrl(staticDataContainer.getS3Repository())
                        .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                        .withOperatorId(sessionRecord.getOperatorId())
                        .withEntityId(sessionRecord.getEntityId())
                        .withEventId(sessionRecord.getIdevento())
                        .build()
                        .buildPath(image);
            } else {
                return "";
            }
        }
        return "".equals(eventImageUrl) ? null : eventImageUrl;
    }

}
