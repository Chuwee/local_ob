package es.onebox.event.catalog;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateConfiguration;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.service.Event2ESService;
import es.onebox.event.catalog.service.PackRefreshService;
import es.onebox.event.catalog.utils.EventMigrationStatus;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class EventMigrationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMigrationProcessor.class);

    private static final int MIGRATION_STATUS_TTL = 10; //min
    private static int REPROCESS_TTL = 5000; //ms
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS'Z'");
    private static final String PROCESSOR_ORIGIN = "ms-event EventMigrationProcessor";

    private final Event2ESService event2ESService;
    private final CacheRepository cacheRepository;
    private final PackRefreshService packRefreshService;

    public EventMigrationProcessor(Event2ESService event2ESService, CacheRepository cacheRepository, PackRefreshService packRefreshService) {
        this.event2ESService = event2ESService;
        this.packRefreshService = packRefreshService;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public void execute(Exchange exchange) throws InterruptedException {
        EventMigrationMessage message = exchange.getIn().getBody(EventMigrationMessage.class);
        if (!isFullRefresh(exchange)) {
            return;
        }

        Long eventId = message.getEventId();
        Long sessionId = message.getSessionId();
        String origin = message.getOrigin();
        EventIndexationType indexationType = getIndexationType(message);
        String executionKey = getEventExecutionKey(eventId, sessionId);

        EventMigrationStatus status = cacheRepository.get(executionKey, EventMigrationStatus.class);
        if (checkDiscardEventMigration(status, message, indexationType)) {
            LOGGER.info("[EVENT CATALOG] eventId: {} - sessionId: {} - type: {} - origin: {} - " +
                            "Discard event migration enqueued at: {} before lastBeginExecution: {}",
                    eventId, sessionId, indexationType, origin,
                    DATE_TIME_FORMATTER.format(ZonedDateTime.parse(message.getEnqueueTime())),
                    DATE_TIME_FORMATTER.format(status.getLastExecutionBeginTime()));
            return;
        }

        if (status == null || !status.isRunning()) {
            status = new EventMigrationStatus(ZonedDateTime.now(), indexationType, true);

            try {
                cacheRepository.set(executionKey, status, MIGRATION_STATUS_TTL, TimeUnit.MINUTES, null);

                StopWatch clock = new StopWatch();
                clock.start();

                Integer updated = event2ESService.updateCatalog(eventId, sessionId, indexationType);

                clock.stop();
                LOGGER.info("[EVENT CATALOG] eventId: {} - sessionId: {} - type: {} - origin: {} - " +
                                "Finished updating event with {} updated documents with total time: {}",
                        eventId, sessionId, indexationType, origin, updated, clock.lastTaskInfo().getTimeMillis());

                LOGGER.info("[MS-EVENT CATALOG] origin: {} - type: {}", origin, indexationType);

                packRefreshService.refreshEventRelatedPacks(eventId, mustUpdatePacks(exchange), PROCESSOR_ORIGIN);

            } catch (Exception e) {
                LOGGER.error("[EVENT CATALOG] eventId: {} - sessionId: {} - origin: {} - Error updating event",
                        eventId, sessionId, origin, e);
            } finally {
                status.setRunning(false);
                cacheRepository.set(executionKey, status, MIGRATION_STATUS_TTL, TimeUnit.MINUTES, null);
            }
        } else {
            LOGGER.info("[EVENT CATALOG] eventId: {} - sessionId: {} - origin: {} - Reprocess after {}ms",
                    eventId, sessionId, origin, REPROCESS_TTL);
            Thread.sleep(REPROCESS_TTL);
            execute(exchange);
        }
    }

    private static String getEventExecutionKey(Long eventId, Long sessionId) {
        String key = "eventMigration_" + eventId;
        if (sessionId != null) {
            key += "_" + sessionId;
        }
        return key;
    }

    private boolean isFullRefresh(Exchange exchange) {
        Object header = exchange.getIn().getHeader(EventMigrationConfiguration.HEADER_REFRESH_ONLY_AVAILABILITY);
        return header == null || !((Boolean) header);
    }

    private static boolean mustUpdatePacks(Exchange exchange) {
        Object header = exchange.getIn().getHeader(CatalogPacksUpdateConfiguration.REFRESH_EVENT_RELATED_PACKS_HEADER);
        return header != null && BooleanUtils.isTrue((Boolean) header);
    }

    private EventIndexationType getIndexationType(EventMigrationMessage message) {
        String refreshType = message.getRefreshType();
        if (refreshType != null) {
            return EventIndexationType.valueOf(refreshType);
        }
        return EventIndexationType.FULL;
    }

    /* Conditions to discard migration
        - Has previous execution in last 10 minutes for this event/session
        - Has been enqueued before the begin execution of the latest migration
        - The indexationType was the same between the current and the latest or latest running type is FULL
     */
    private boolean checkDiscardEventMigration(EventMigrationStatus status, EventMigrationMessage message, EventIndexationType indexationType) {
        return status != null && message.getEnqueueTime() != null &&
                ZonedDateTime.parse(message.getEnqueueTime()).isBefore(status.getLastExecutionBeginTime()) &&
                (indexationType.equals(status.getIndexationType()) || EventIndexationType.FULL.equals(status.getIndexationType()));
    }

}
