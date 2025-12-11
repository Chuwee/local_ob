package es.onebox.event.catalog.amqp.catalogoccupation;

import es.onebox.event.catalog.EventMigrationMessage;
import es.onebox.event.catalog.service.Event2ESService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static es.onebox.event.catalog.EventMigrationConfiguration.HEADER_REFRESH_ONLY_AVAILABILITY;

@Component
public class CatalogOccupationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogOccupationProcessor.class);

    private final Event2ESService event2ESService;

    @Autowired
    public CatalogOccupationProcessor(Event2ESService event2ESService) {
        this.event2ESService = event2ESService;
    }

    @Override
    public void execute(Exchange exchange) {
        EventMigrationMessage message = exchange.getIn().getBody(EventMigrationMessage.class);
        Long eventId = message.getEventId();
        Long sessionId = message.getSessionId();
        String origin = message.getOrigin();
        boolean allSessions = message.isAllSessions();

        if (!isOnlyAvailability(exchange)) {
            return;
        }

        try {
            StopWatch clock = new StopWatch();
            clock.start();

            Integer updated = event2ESService.updateOccupation(eventId, allSessions ? null : sessionId);

            clock.stop();
            LOGGER.info("[EVENT OCCUPATION] eventId: {} - sessionId: {} - Finished updating occupation with {} updated child documents with total time: {}",
                    eventId, sessionId, updated, clock.lastTaskInfo().getTimeMillis());

            LOGGER.info("[MS-EVENT OCCUPATION] origin: {}", origin);
        } catch (Exception e) {
            LOGGER.error("[EVENT OCCUPATION] eventId: {} - sessionId: {} - Error updating occupation",
                    eventId, sessionId, e);
        }
    }

    private boolean isOnlyAvailability(Exchange exchange) {
        return exchange.getIn().getHeader(HEADER_REFRESH_ONLY_AVAILABILITY) != null &&
                BooleanUtils.isTrue((Boolean) exchange.getIn().getHeader(HEADER_REFRESH_ONLY_AVAILABILITY));
    }
}
