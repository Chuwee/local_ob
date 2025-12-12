package es.onebox.ms.notification.webhooks;

import es.onebox.ms.notification.webhooks.dao.NotificationConfigDao;
import es.onebox.ms.notification.webhooks.dto.CreateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfig;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigs;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigsDTO;
import es.onebox.ms.notification.webhooks.dto.SearchNotificationConfigFilterDTO;
import es.onebox.ms.notification.webhooks.dto.UpdateNotificationConfigDTO;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderWebhookServiceTest {

    private static final String URI_NOTIFICATIONS = WebhookController.BASE_URI;
    private static final String URI_NOTIFICATION_APIKEY = WebhookController.BASE_URI + "/regenerate_apikey";

    @Mock
    private NotificationConfigDao notificationConfigDao;
    @InjectMocks
    private WebhookService service;

    private static final String DOCUMENT_ID = "1234567890-qwerty";

    @BeforeEach
    public void initOpenApi() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getNotificationConfigTest() {
        NotificationConfig config = ObjectRandomizer.random(NotificationConfig.class);

        Mockito.doReturn(config).when(notificationConfigDao).get(DOCUMENT_ID);

        NotificationConfigDTO response = service.getNotificationConfig(DOCUMENT_ID);

        Assertions.assertEquals(config.getApiKey(), response.getApiKey());
    }

    @Test
    public void getNotificationConfigsTest() {
        NotificationConfigs configs = ObjectRandomizer.random(NotificationConfigs.class);
        SearchNotificationConfigFilterDTO filter = ObjectRandomizer.random(SearchNotificationConfigFilterDTO.class);

        Mockito.doReturn(configs).when(notificationConfigDao).advancedGet(filter);

        NotificationConfigsDTO response = service.getNotificationConfigs(filter);

        Assertions.assertNotNull(response);
        verify(notificationConfigDao).advancedGet(filter);
    }

    @Test
    public void createNotificationConfigTest() {
        CreateNotificationConfigDTO createDto = ObjectRandomizer.random(CreateNotificationConfigDTO.class);

        Mockito.doReturn(null).when(notificationConfigDao).get(Mockito.anyString());
        Mockito.doNothing().when(notificationConfigDao).upsert(Mockito.any(NotificationConfig.class));

        service.createNotificationConfig(createDto);

        verify(notificationConfigDao, times(2)).get(Mockito.anyString());
        verify(notificationConfigDao).upsert(Mockito.any(NotificationConfig.class));
    }

    @Test
    public void updateNotificationConfigTest() {
        NotificationConfig config = ObjectRandomizer.random(NotificationConfig.class);
        UpdateNotificationConfigDTO updateDto = ObjectRandomizer.random(UpdateNotificationConfigDTO.class);

        Mockito.doReturn(config).when(notificationConfigDao).get(DOCUMENT_ID);
        Mockito.doNothing().when(notificationConfigDao).upsert(Mockito.any(NotificationConfig.class));

        service.updateNotificationConfig(DOCUMENT_ID, updateDto);

        verify(notificationConfigDao).get(DOCUMENT_ID);
        verify(notificationConfigDao).upsert(Mockito.any(NotificationConfig.class));
    }

    @Test
    public void deleteNotificationConfigTest() {
        NotificationConfig config = ObjectRandomizer.random(NotificationConfig.class);

        Mockito.doReturn(config).when(notificationConfigDao).get(DOCUMENT_ID);
        Mockito.doNothing().when(notificationConfigDao).remove(DOCUMENT_ID);

        service.deleteNotificationConfig(DOCUMENT_ID);

        verify(notificationConfigDao).get(DOCUMENT_ID);
        verify(notificationConfigDao).remove(DOCUMENT_ID);
    }

    @Test
    public void regenerateApiKeyTest() {
        NotificationConfig config = ObjectRandomizer.random(NotificationConfig.class);

        Mockito.doReturn(config).when(notificationConfigDao).get(DOCUMENT_ID);
        Mockito.doNothing().when(notificationConfigDao).upsert(config);

        NotificationConfigDTO response = service.regenerateApiKey(DOCUMENT_ID);

        Assertions.assertEquals(config.getApiKey(), response.getApiKey());
    }
}
