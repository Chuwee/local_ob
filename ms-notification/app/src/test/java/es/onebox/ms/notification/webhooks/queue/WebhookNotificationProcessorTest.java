package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolDTO;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolsDTO;
import es.onebox.ms.notification.datasources.ms.channel.enums.ChannelExternalToolsNamesDTO;
import es.onebox.ms.notification.webhooks.WebhookSendingService;
import es.onebox.ms.notification.webhooks.WebhookService;
import es.onebox.ms.notification.webhooks.dto.EntityPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.EventNotificationMessage;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.ProductPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.WrapperDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationAction;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import es.onebox.ms.notification.webhooks.enums.NotificationsScope;
import es.onebox.ms.notification.webhooks.enums.NotificationsStatus;
import es.onebox.ms.notification.webhooks.utils.NotifierDispatcher;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebhookNotificationProcessorTest {

    @Mock
    private NotifierDispatcher dispatcher;
    @Mock
    private WebhookService webhookService;
    @Mock
    private WebhookSendingService webhookSendingService;
    @Mock
    private DefaultProducer webhookNotificationErrorProducer;

    @InjectMocks
    private WebhookNotificationsProcessor webhookNotificationsProcessor;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void execute_ok() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("ENTITY_FVZONE", "CREATE");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfigs = generateNotificationConfig(message.getId(),
                "ENTITY_FVZONE", "CREATE", NotificationsScope.ENTITY, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfig("ENTITY_FVZONE_CREATE")).thenReturn(notificationConfigs);
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);
        when(dispatcher.getWrapper(any())).thenReturn(generateEntityWrapper(message));

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(1)).sendNotification(any(), any());
    }

    @Test
    public void execute_differentNotificationConfigEvent_doNothing() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("ENTITY_FVZONE", "CREATE");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfig = generateNotificationConfig(message.getId(),
                "EVENT", "CREATE", NotificationsScope.ENTITY, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfig("ENTITY_FVZONE_CREATE")).thenReturn(notificationConfig);
        when(dispatcher.getWrapper(any())).thenReturn(generateEntityWrapper(message));
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(0)).sendNotification(any(), any());
    }

    @Test
    public void execute_differentNotificationConfigAction_doNothing() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("ENTITY_FVZONE", "CREATE");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfigs = generateNotificationConfig(message.getId(),
                "ENTITY_FVZONE", "BOOKING", null, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfig("ENTITY_FVZONE_CREATE")).thenReturn(notificationConfigs);
        when(dispatcher.getWrapper(any())).thenReturn(generateEntityWrapper(message));
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(0)).sendNotification(any(), any());
    }

    private EventNotificationMessage generateNotificationMessage(String event, String action) {
        EventNotificationMessage notificationMessage = new EventNotificationMessage();
        notificationMessage.setEvent(event);
        notificationMessage.setAction(action);
        notificationMessage.setId(1L);
        return notificationMessage;
    }

    private Exchange generateMockedExchange(EventNotificationMessage notificationMessage) {
        Message message = Mockito.mock(Message.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(any())).thenReturn(notificationMessage);
        return exchange;
    }

    private NotificationConfigDTO generateNotificationConfig(Long entityId, String event, String action, NotificationsScope scope, NotificationsStatus status) {
        Map<NotificationType, List<String>> notification = new HashMap<>();
        notification.put(NotificationType.valueOf(event), List.of(action));

        NotificationConfigDTO notificationConfig = new NotificationConfigDTO();
        notificationConfig.setEntityId(entityId);
        notificationConfig.setEvents(notification);
        notificationConfig.setScope(scope);
        notificationConfig.setStatus(status);

        return notificationConfig;
    }

    private WrapperDTO generateEntityWrapper(EventNotificationMessage notificationMessage) {
        EntityPayloadDTO payload = new EntityPayloadDTO();
        payload.setId(notificationMessage.getId());
        payload.setAction(NotificationAction.valueOf(notificationMessage.getAction()));
        payload.setEvent(notificationMessage.getEvent());

        WrapperDTO wrapperDTO = new WrapperDTO();
        wrapperDTO.setEntityId(notificationMessage.getId());
        wrapperDTO.setPayloadRequest(payload);

        return wrapperDTO;
    }

    @Test
    public void execute_PRODUCT_ok() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("PRODUCT", "CATALOG");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfig = generateNotificationConfig(message.getId(),
                "PRODUCT", "CATALOG", NotificationsScope.ENTITY, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfigs(message.getId())).thenReturn(List.of(notificationConfig));
        when(dispatcher.getWrapper(any())).thenReturn(generateProductWrapper(message));
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(1)).sendNotification(any(), any());
    }

    @Test
    public void execute_differentNotificationConfigEvent_PRODUCT_doNothing() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("PRODUCT", "CATALOG");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfig = generateNotificationConfig(message.getId(),
                "EVENT", "CATALOG", NotificationsScope.ENTITY, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfigs(message.getId())).thenReturn(List.of(notificationConfig));
        when(dispatcher.getWrapper(any())).thenReturn(generateProductWrapper(message));
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(0)).sendNotification(any(), any());
    }

    @Test
    public void execute_differentNotificationConfigAction_PRODUCT_doNothing() throws Exception {
        EventNotificationMessage message = generateNotificationMessage("PRODUCT", "CATALOG");
        Exchange exchange = generateMockedExchange(message);
        ChannelExternalToolsDTO channelExternalToolsDTO = new ChannelExternalToolsDTO();
        ChannelExternalToolDTO channelExternalToolDTO = new ChannelExternalToolDTO();
        channelExternalToolDTO.setName(ChannelExternalToolsNamesDTO.GTM);
        channelExternalToolDTO.setEnabled(false);
        channelExternalToolsDTO.add(channelExternalToolDTO);

        NotificationConfigDTO notificationConfig = generateNotificationConfig(message.getId(),
                "PRODUCT", "PURCHASE", null, NotificationsStatus.ACTIVE);

        when(webhookService.getNotificationConfigs(message.getId())).thenReturn(List.of(notificationConfig));
        when(dispatcher.getWrapper(any())).thenReturn(generateProductWrapper(message));
        when(webhookService.getChannelExternalTool(any())).thenReturn(channelExternalToolsDTO);

        webhookNotificationsProcessor.execute(exchange);
        verify(webhookSendingService, times(0)).sendNotification(any(), any());
    }

    private WrapperDTO generateProductWrapper(EventNotificationMessage notificationMessage) {
        ProductPayloadDTO payload = new ProductPayloadDTO();
        payload.setId(notificationMessage.getId());
        payload.setAction(NotificationAction.valueOf(notificationMessage.getAction()));
        payload.setEvent(notificationMessage.getEvent());

        WrapperDTO wrapperDTO = new WrapperDTO();
        wrapperDTO.setEntityId(notificationMessage.getId());
        wrapperDTO.setPayloadRequest(payload);

        return wrapperDTO;
    }

}
