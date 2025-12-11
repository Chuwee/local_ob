package es.onebox.event.common.amqp.refreshdata;

import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateConfiguration;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RefreshDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshDataService.class);
    private static final String APP = "ms-event ";
    private static final int BULK_THRESHOLD = 1;

    private static final List<String> SUPPORTED_EVENT_PARTIAL_REFRESH = List.of("status",
            "contactPersonName", "contactPersonSurname", "contactPersonEmail", "contactPersonPhone",
            "salesGoalTickets", "salesGoalRevenue", "allowGroups", "groupPrice", "groupCompanionPayment");
    private static final List<String> SUPPORTED_SESSION_PARTIAL_REFRESH = List.of("name", "status", "date",
            "reference", "saleType", "enableChannels", "enableBookings", "enableSales");
    private static final List<String> SKIP_SESSION_REFRESH = List.of("capacity");
    private static final List<String> RESERVED_FIELDS = List.of("id", "serialVersionUID");
    private static final List<String> SUPPORTED_PACK_REFRESH = List.of("status");

    private final DefaultProducer refreshDataProducer;
    private final DefaultProducer productMigrationProducer;

    @Autowired
    public RefreshDataService(@Qualifier("refreshDataProducer") DefaultProducer refreshDataProducer,
                              @Qualifier("productMigrationProducer") DefaultProducer productMigrationProducer) {
        this.refreshDataProducer = refreshDataProducer;
        this.productMigrationProducer = productMigrationProducer;
    }

    public void refreshEvent(Long eventId, String origin) {
        this.sendEventRefreshMessage(RefreshDataMessage.Type.EVENT_REFRESH, eventId, APP + origin + " refreshEvent", EventIndexationType.FULL, true);
    }

    public void refreshEvent(Long eventId, String origin, EventIndexationType refreshType) {
        this.sendEventRefreshMessage(RefreshDataMessage.Type.EVENT_REFRESH, eventId, APP + origin + " refreshEvent", refreshType);
    }

    public void refreshEvent(Long eventId, String origin, EventIndexationType refreshType, boolean refreshPacks) {
        this.sendEventRefreshMessage(RefreshDataMessage.Type.EVENT_REFRESH, eventId, APP + origin + " refreshEvent", refreshType, refreshPacks);
    }

    public void refreshEvent(Long eventId, String origin, UpdateEventRequestDTO request) {
        EventIndexationType refreshType = getRefreshType(request, SUPPORTED_EVENT_PARTIAL_REFRESH);
        boolean refreshPacks = getRefreshPack(request);
        this.sendEventRefreshMessage(RefreshDataMessage.Type.EVENT_REFRESH, eventId, APP + origin + " refreshEvent", refreshType, refreshPacks);
    }

    public void refreshSession(Long sessionId, String origin) {
        this.sendEventRefreshMessage(RefreshDataMessage.Type.SESSION_REFRESH, sessionId, APP + origin + " refreshSession", EventIndexationType.FULL, true);
    }

    public void refreshSession(Long sessionId, String origin, EventIndexationType refreshType) {
        this.sendEventRefreshMessage(RefreshDataMessage.Type.SESSION_REFRESH, sessionId, APP + origin + " refreshSession", refreshType);
    }

    public void refreshSessions(Long eventId, List<Long> sessionIds, String origin) {
        this.refreshSessions(eventId, sessionIds, origin, EventIndexationType.FULL);
    }

    public void refreshSessions(Long eventId, List<Long> sessionIds, String origin, UpdateSessionRequestDTO request) {
        List<String> skipFields = checkSkipRefresh(request, SKIP_SESSION_REFRESH);
        if (CollectionUtils.isNotEmpty(skipFields)) {
            LOGGER.info("[REFRESH DATA] eventId: {} - skip refresh sessions: {} - updated fields: {}", eventId, sessionIds, skipFields);
            return;
        }
        EventIndexationType refreshType = getRefreshType(request, SUPPORTED_SESSION_PARTIAL_REFRESH);
        this.refreshSessions(eventId, sessionIds, origin, refreshType);
    }

    public void refreshSessions(Long eventId, List<Long> sessionIds, String origin, EventIndexationType refreshType) {
        if (CollectionUtils.isNotEmpty(sessionIds)) {
            boolean refreshPacks = EventIndexationType.FULL.equals(refreshType);
            Set<Long> uniqueSessions = new HashSet<>(sessionIds);
            if (uniqueSessions.size() <= BULK_THRESHOLD) {
                for (Long sessionId : uniqueSessions) {
                    this.sendEventRefreshMessage(RefreshDataMessage.Type.SESSION_REFRESH, sessionId, APP + origin + " refreshSession", refreshType, refreshPacks);
                }
            } else {
                this.sendEventRefreshMessage(RefreshDataMessage.Type.EVENT_REFRESH, eventId, APP + origin + " refreshEvent", refreshType, refreshPacks);
            }
        }
    }

    public void refreshProduct(Long productId) {
        sendEventProductMessage(productId);
    }

    private void sendEventRefreshMessage(RefreshDataMessage.Type type, Long id, String origin, EventIndexationType refreshType, boolean refreshPacks) {
        Map<String, Object> headers = null;
        if (refreshPacks) {
            headers = new HashMap<>();
            headers.put(CatalogPacksUpdateConfiguration.REFRESH_EVENT_RELATED_PACKS_HEADER, true);
        }
        sendEventRefreshMessage(type, id, origin, refreshType, headers);
    }

    private void sendEventRefreshMessage(RefreshDataMessage.Type type, Long id, String origin, EventIndexationType refreshType) {
        sendEventRefreshMessage(type, id, origin, refreshType, null);
    }

    private void sendEventRefreshMessage(RefreshDataMessage.Type type, Long id, String origin, EventIndexationType refreshType, Map<String, Object> headers) {
        RefreshDataMessage message = new RefreshDataMessage();
        message.setId(id);
        message.setType(type);
        message.setOrigin(origin);
        if (refreshType != null) {
            message.setRefreshType(refreshType.name());
        }

        try {
            refreshDataProducer.sendMessage(message, headers);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] RefreshData Message could not be send for event: " + id, e);
        }
    }

    private void sendEventProductMessage(Long productId) {
        ProductMigrationMessage productMigrationMessage = new ProductMigrationMessage();
        productMigrationMessage.setProductId(productId);
        try {
            productMigrationProducer.sendMessage(productMigrationMessage);
        } catch (Exception e) {
            LOGGER.error("[AMQP CLIENT] migrateProduct message could not be send", e);
        }
    }

    private List<String> checkSkipRefresh(UpdateSessionRequestDTO request, List<String> skipSessionRefreshFields) {
        List<String> skipFiels = new ArrayList<>();
        for (String updateField : getUpdatedFields(request)) {
            if (skipSessionRefreshFields.contains(updateField)) {
                skipFiels.add(updateField);
            } else {
                skipFiels.clear();
                break;
            }
        }
        return skipFiels;
    }

    private EventIndexationType getRefreshType(Object request, List<String> supportedPartialUpdate) {
        List<String> partialUpdate = new ArrayList<>();
        for (String updateField : getUpdatedFields(request)) {
            if (supportedPartialUpdate.contains(updateField)) {
                partialUpdate.add(updateField);
            } else {
                partialUpdate.clear();
                break;
            }
        }
        return CollectionUtils.isNotEmpty(partialUpdate) ? EventIndexationType.PARTIAL_BASIC : EventIndexationType.FULL;
    }

    private static boolean getRefreshPack(Object request) {
        for (String updateField : getUpdatedFields(request)) {
            if (SUPPORTED_PACK_REFRESH.contains(updateField)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getUpdatedFields(Object obj) {
        List<String> nonNullFields = new ArrayList<>();
        if (obj != null) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.get(obj) != null && !RESERVED_FIELDS.contains(field.getName())) {
                        nonNullFields.add(field.getName());
                    }
                } catch (IllegalAccessException e) {
                    //ignore
                }
            }
        }
        return nonNullFields;
    }

}
