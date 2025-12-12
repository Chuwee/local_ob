package es.onebox.ms.notification.webhooks;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.webhooks.dao.NotificationConfigDao;
import es.onebox.ms.notification.webhooks.dto.CreateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfig;
import es.onebox.ms.notification.webhooks.dto.UpdateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebhooksServiceTest {

    private final static String DOCUMENT_ID = "1";

    @Mock
    private NotificationConfigDao notificationConfigDao;

    @InjectMocks
    private WebhookService webhookService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateNotificationConfig_ENTITY_ok() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("CREATE"));
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("UPDATE"));

        NotificationConfig config = new NotificationConfig();
        config.setDocumentId(DOCUMENT_ID);
        config.setEvents(events);

        UpdateNotificationConfigDTO updateNotificationConfig = new UpdateNotificationConfigDTO();
        updateNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(config.getDocumentId())).thenReturn(config);
        webhookService.updateNotificationConfig(DOCUMENT_ID, updateNotificationConfig);
        verify(notificationConfigDao, times(1)).upsert((any()));
    }

    @Test
    public void updateNotificationConfig_ENTITY_ko() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("CREATE"));
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("UPDATE"));

        NotificationConfig config = new NotificationConfig();
        config.setDocumentId(DOCUMENT_ID);
        config.setEvents(events);

        Map<NotificationType, List<String>> updateEvents = new HashMap<>();
        updateEvents.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("CATALOG"));

        UpdateNotificationConfigDTO updateNotificationConfig = new UpdateNotificationConfigDTO();
        updateNotificationConfig.setEvents(updateEvents);

        when(notificationConfigDao.get(config.getDocumentId())).thenReturn(config);

        OneboxRestException exc =  assertThrows( OneboxRestException.class,
                ()-> webhookService.updateNotificationConfig(DOCUMENT_ID, updateNotificationConfig));
        assertEquals(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL.getErrorCode(), exc.getErrorCode());
    }

    @Test
    public void createNotificationConfig_ENTITY_ok() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("CREATE"));

        CreateNotificationConfigDTO createNotificationConfig = new CreateNotificationConfigDTO();
        createNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(anyString())).thenReturn(null);

        webhookService.createNotificationConfig(createNotificationConfig);
        verify(notificationConfigDao, times(1)).upsert((any()));
    }

    @Test
    public void createNotificationConfig_ENTITY_ko() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("ENTITY_FVZONE"), List.of("CATALOG"));

        CreateNotificationConfigDTO createNotificationConfig = new CreateNotificationConfigDTO();
        createNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(anyString())).thenReturn(null);

        OneboxRestException exc =  assertThrows( OneboxRestException.class,
                ()-> webhookService.createNotificationConfig(createNotificationConfig));
        assertEquals(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL.getErrorCode(), exc.getErrorCode());
    }

    @Test
    public void updateNotificationConfig_PRODUCT_ok() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("PRODUCT"), List.of("CATALOG"));

        NotificationConfig config = new NotificationConfig();
        config.setDocumentId(DOCUMENT_ID);
        config.setEvents(events);

        UpdateNotificationConfigDTO updateNotificationConfig = new UpdateNotificationConfigDTO();
        updateNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(config.getDocumentId())).thenReturn(config);
        webhookService.updateNotificationConfig(DOCUMENT_ID, updateNotificationConfig);
        verify(notificationConfigDao, times(1)).upsert((any()));
    }

    @Test
    public void updateNotificationConfig_PRODUCT_ko() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("PRODUCT"), List.of("CATALOG"));

        NotificationConfig config = new NotificationConfig();
        config.setDocumentId(DOCUMENT_ID);
        config.setEvents(events);

        Map<NotificationType, List<String>> updateEvents = new HashMap<>();
        updateEvents.put(NotificationType.valueOf("PRODUCT"), List.of("PURCHASE"));

        UpdateNotificationConfigDTO updateNotificationConfig = new UpdateNotificationConfigDTO();
        updateNotificationConfig.setEvents(updateEvents);

        when(notificationConfigDao.get(config.getDocumentId())).thenReturn(config);

        OneboxRestException exc =  assertThrows( OneboxRestException.class,
                ()-> webhookService.updateNotificationConfig(DOCUMENT_ID, updateNotificationConfig));
        assertEquals(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL.getErrorCode(), exc.getErrorCode());
    }

    @Test
    public void createNotificationConfig_PRODUCT_ok() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("PRODUCT"), List.of("CATALOG"));

        CreateNotificationConfigDTO createNotificationConfig = new CreateNotificationConfigDTO();
        createNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(anyString())).thenReturn(null);

        webhookService.createNotificationConfig(createNotificationConfig);
        verify(notificationConfigDao, times(1)).upsert((any()));
    }

    @Test
    public void createNotificationConfig_PRODUCT_ko() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.valueOf("PRODUCT"), List.of("PURCHASE"));

        CreateNotificationConfigDTO createNotificationConfig = new CreateNotificationConfigDTO();
        createNotificationConfig.setEvents(events);

        when(notificationConfigDao.get(anyString())).thenReturn(null);

        OneboxRestException exc =  assertThrows( OneboxRestException.class,
                ()-> webhookService.createNotificationConfig(createNotificationConfig));
        assertEquals(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL.getErrorCode(), exc.getErrorCode());
    }


}
