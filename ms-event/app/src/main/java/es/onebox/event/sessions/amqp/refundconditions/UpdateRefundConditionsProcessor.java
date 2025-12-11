package es.onebox.event.sessions.amqp.refundconditions;

import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionRefundConditionsService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UpdateRefundConditionsProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRefundConditionsProcessor.class);

    private final SessionDao sessionDao;
    private final SessionRefundConditionsService refundConditionsService;

    @Autowired
    public UpdateRefundConditionsProcessor(final SessionDao sessionDao,
                                           final SessionRefundConditionsService refundConditionsService) {
        this.sessionDao = sessionDao;
        this.refundConditionsService = refundConditionsService;
    }

    @Override
    public void execute(Exchange exchange) {
        UpdateRefundConditionsMessage message = exchange.getIn().getBody(UpdateRefundConditionsMessage.class);

        LOGGER.info("[SESSION PACKS REFUND CONDITIONS] Consuming message with EventId {} and VenueTemplateId {}",
                message.getEventId(), message.getVenueTemplateId());

        List<SessionRecord> sessions = getSessions(message);
        if (sessions.isEmpty()) {
            LOGGER.info("[SESSION PACKS REFUND CONDITIONS] None sessions require an update",
                    message.getEventId(), message.getVenueTemplateId());
            return;
        }

        sessions.stream().forEach(session -> updateSessionRefundConditionMap(session.getIdsesion().longValue(),
                message.getVenueTemplateId()));
        LOGGER.info("[SESSION PACKS REFUND CONDITIONS] All sessions updated for EventId {} and VenueTemplateId {}",
                message.getEventId(), message.getVenueTemplateId());
    }

    private void updateSessionRefundConditionMap(final Long sessionId, final Long venueTemplateId) {
        try {
            refundConditionsService.updateRefundConditionsMap(sessionId, venueTemplateId);
        } catch (Exception ex) {
            LOGGER.info("[SESSION PACKS REFUND CONDITIONS] Error while updating session {} refund conditions",sessionId);
        }
    }

    private List<SessionRecord> getSessions(final UpdateRefundConditionsMessage message) {
        SessionSearchFilter sessionsFilter = new SessionSearchFilter();
        sessionsFilter.setEventId(Collections.singletonList(message.getEventId()));
        sessionsFilter.setVenueConfigId(message.getVenueTemplateId());
        sessionsFilter.setSessionPack(Boolean.TRUE);
        sessionsFilter.setAllowPartialRefund(Boolean.TRUE);

        return sessionDao.findSessions(sessionsFilter, null);
    }
}
