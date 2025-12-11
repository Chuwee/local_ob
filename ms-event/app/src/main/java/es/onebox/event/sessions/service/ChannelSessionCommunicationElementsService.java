package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.services.CommonChannelCommunicationElementService;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.core.utils.common.CommonUtils.isNull;


@Service
public class ChannelSessionCommunicationElementsService {

    private final CommonChannelCommunicationElementService commonChannelCommunicationElementService;
    private final ChannelEventDao channelEventDao;
    private final SessionValidationHelper sessionValidationHelper;


    @Autowired
    public ChannelSessionCommunicationElementsService(CommonChannelCommunicationElementService commonChannelCommunicationElementService, ChannelEventDao channelEventDao, SessionValidationHelper sessionValidationHelper) {

        this.commonChannelCommunicationElementService = commonChannelCommunicationElementService;
        this.channelEventDao = channelEventDao;
        this.sessionValidationHelper = sessionValidationHelper;
    }

    @MySQLRead
    public List<EventCommunicationElementDTO> findCommunicationElements(Long eventId, Long sessionId, Long channelId,
                                                                        ChannelEventCommunicationElementFilter filter) {

        EventChannelRecord channelEvent = getAndCheckChannelEvent(eventId, sessionId, channelId);
        return commonChannelCommunicationElementService.findCommunicationElements(channelEvent, sessionId, filter);
    }

    @MySQLWrite
    public void updateChannelSessionCommunicationElements(Long eventId, Long sessionId, Long channelId, List<EventCommunicationElementDTO> elements) {
        EventChannelRecord channelEvent = getAndCheckChannelEvent(eventId, sessionId, channelId);
        commonChannelCommunicationElementService.updateChannelCommunicationElements(channelEvent, sessionId, elements);
    }

    @MySQLRead
    private EventChannelRecord getAndCheckChannelEvent(Long eventId, Long sessionId, Long channelId){
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

        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        return eventChannelDetail;
    }
}
