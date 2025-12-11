package es.onebox.event.events.service;

import com.couchbase.client.core.api.search.queries.CoreGeoCoordinates;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.services.CommonChannelCommunicationElementService;
import es.onebox.event.events.dto.ChannelEventImageConfigDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.enums.ImageOrigin;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.isNull;

@Service
public class ChannelEventCommunicationElementsService {

    private final ChannelEventDao channelEventDao;
    private final EventService eventService;
    private final SessionService sessionService;
    private final CommonChannelCommunicationElementService commonChannelCommunicationElementService;

    @Autowired
    public ChannelEventCommunicationElementsService (ChannelEventDao channelEventDao, EventService eventService, SessionService sessionService,
                                                     CommonChannelCommunicationElementService commonChannelCommunicationElementService) {
        this.channelEventDao = channelEventDao;
        this.eventService = eventService;
        this.sessionService = sessionService;
        this.commonChannelCommunicationElementService = commonChannelCommunicationElementService;
    }

    public List<EventCommunicationElementDTO> findCommunicationElements(Long eventId, Long channelId, ChannelEventCommunicationElementFilter filter) {
        EventChannelRecord channelEvent = getAndCheckChannelEvent(eventId, channelId);
        return commonChannelCommunicationElementService.findCommunicationElements(channelEvent, null, filter);
    }

    public List<ChannelEventImageConfigDTO> getChannelEventImagesConfiguration(Long eventId, Long channelId) {
        EventChannelRecord channelEvent = getAndCheckChannelEvent(eventId, channelId);
        EventDTO event = eventService.getEvent(eventId);
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        SortOperator<String> sortOperator = new SortOperator<>();
        sortOperator.addDirection(Direction.DESC, "date");
        sessionSearchFilter.setSort(sortOperator);
        if (Boolean.FALSE.equals(event.getSupraEvent())) {
            sessionSearchFilter.setSessionPack(true);
        }
        SessionsDTO sessions =  sessionService.searchSessions(eventId, sessionSearchFilter);
        if (sessions.getMetadata().getTotal() == 0) {
            return new ArrayList<>();
        }

        return commonChannelCommunicationElementService.buildChannelEventImageConfig(channelEvent, sessions);
    }


    @MySQLWrite
    public void updateChannelEventCommunicationElements(Long eventId, Long channelId, List<EventCommunicationElementDTO> elements) {
        EventChannelRecord channelEvent = getAndCheckChannelEvent(eventId, channelId);
        commonChannelCommunicationElementService.updateChannelCommunicationElements(channelEvent, null, elements);
    }

    @MySQLRead
    private EventChannelRecord getAndCheckChannelEvent(Long eventId, Long channelId){
        if (isNull(eventId)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (isNull(channelId)) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_ID_MANDATORY);
        }

        EventChannelRecord eventChannelDetail =  channelEventDao.getChannelEventDetailed(channelId.intValue(), eventId.intValue());
        if (isNull(eventChannelDetail)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }
        return eventChannelDetail;
    }
}
