package es.onebox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.common.datasources.ms.channel.ChannelConfigResponse;
import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.ProductChannelsDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import es.onebox.common.datasources.ms.notification.repository.MsNotificationRepository;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.webhook.WebhookDatasource;
import es.onebox.common.datasources.webhook.dto.fever.AllowedEntitiesFileData;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaMessage;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaPayload;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.fever.config.FeverConfig;
import es.onebox.fever.config.FeverWebhookConfiguration;
import es.onebox.fever.repository.AllowedEntitiesRepository;
import es.onebox.fever.service.ChannelWebhookService;
import es.onebox.fever.service.EntityWebhookService;
import es.onebox.fever.service.EventWebhookService;
import es.onebox.fever.service.OrderWebhookService;
import es.onebox.fever.service.ProductWebhookService;
import es.onebox.fever.service.PromotionWebhookService;
import es.onebox.fever.service.SessionWebhookService;
import es.onebox.fever.service.UserWebhookService;
import es.onebox.fever.service.WebhookService;
import es.onebox.message.broker.kafka.DefaultKafkaProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebhookServiceTest {

    @Mock
    private WebhookDatasource webhookDatasource;
    @Mock
    private MsNotificationRepository msNotificationRepository;
    @Mock
    private PromotionWebhookService promotionWebhookService;
    @Mock
    private EventWebhookService eventWebhookService;
    @Mock
    private ProductWebhookService productWebhookService;
    @Mock
    private MsEventRepository msEventRepository;
    @Mock
    private MsOrderRepository msOrderRepository;
    @Mock
    private MsChannelDatasource msChannelDatasource;
    @Mock
    private SessionWebhookService sessionWebhookService;
    @Mock
    private OrderWebhookService orderWebhookService;
    @Mock
    private ChannelWebhookService channelWebhookService;
    @Mock
    private FeverWebhookConfiguration feverWebhookConfiguration;
    @Mock
    private UserWebhookService userWebhookService;
    @Mock
    private AllowedEntitiesRepository allowedEntitiesRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private EntityWebhookService entityWebhookService;
    @Mock
    private DefaultKafkaProducer feverKafkaProducer;

    @InjectMocks
    private WebhookService webhookService;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Setup AllowedEntitiesFileData mock
        AllowedEntitiesFileData allowedEntitiesFileData = new AllowedEntitiesFileData();
        allowedEntitiesFileData.setEntityId(1L);
        allowedEntitiesFileData.setAllowedEntities(List.of(1L, 2L, 3L));

        when(allowedEntitiesRepository.getAllowedEntitiesFileData(any())).thenReturn(allowedEntitiesFileData);

        // Setup channel config mocks for validation
        ChannelConfigResponse channelConfigResponse = new ChannelConfigResponse();
        ChannelConfigDTO channelConfig = new ChannelConfigDTO();
        channelConfig.setEntityId(1L);
        channelConfig.setWhitelabelType(WhitelabelType.EXTERNAL);
        channelConfigResponse.setData(List.of(channelConfig));

        when(msChannelDatasource.getChannelConfigs(any())).thenReturn(channelConfigResponse);
    }

    @Test
    public void sendEntityEventNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessage("ENTITY_FVZONE");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(webhookDatasource, times(1)).sendFeverMessage(eq(message), any(), any());
    }

    @Test
    public void sendEntityEventNotification_notAllowed_doNothing() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessage("ENTITY_FVZONE");

        WebhookFeverDTO messageNotAllowed = generateWebhookMessage("ENTITY_FVZONE");
        messageNotAllowed.setAllowSend(Boolean.FALSE);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(messageNotAllowed);

        webhookService.sendWebhookToFever(message);

        verify(feverWebhookConfiguration, times(0)).getHashKey();
        verify(webhookDatasource, times(0)).sendFeverMessage(eq(message), any(), any());
    }

    @Test
    public void sendProductGeneralDataNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_GENERAL_DATA");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductGeneralData(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductGeneralData(any());
    }

    @Test
    public void sendProductSurchargesNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_SURCHARGES");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductSurcharges(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductSurcharges(any());
    }

    @Test
    public void sendProductConfigurationNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_CONFIGURATION");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductConfiguration(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductConfiguration(any());
    }

    @Test
    public void sendProductLanguagesNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_LANGUAGES");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductLanguages(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductLanguages(any());
    }

    @Test
    public void sendProductEventsNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_EVENTS");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductEvents(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductEvents(any());
    }

    @Test
    public void sendProductSessionsNotification_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_SESSIONS");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductSessions(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductSessions(any());
    }

    @Test
    public void sendProductEventsNotification_notAllowed_doNothing() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_EVENTS");

        WebhookFeverDTO messageNotAllowed = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_EVENTS");
        messageNotAllowed.setAllowSend(Boolean.FALSE);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductEvents(any())).thenReturn(messageNotAllowed);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductEvents(any());
        verify(feverWebhookConfiguration, times(0)).getHashKey();
        verify(webhookDatasource, times(0)).sendFeverMessage(eq(message), any(), any());
    }

    @Test
    public void sendProductSessionsNotification_notAllowed_doNothing() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_SESSIONS");

        WebhookFeverDTO messageNotAllowed = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_SESSIONS");
        messageNotAllowed.setAllowSend(Boolean.FALSE);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductSessions(any())).thenReturn(messageNotAllowed);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductSessions(any());
        verify(feverWebhookConfiguration, times(0)).getHashKey();
        verify(webhookDatasource, times(0)).sendFeverMessage(eq(message), any(), any());
    }

    @Test
    public void sendProductWebhook_unknownSubtype_throwsException() {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "UNKNOWN_SUBTYPE");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());

        Assertions.assertThrows(OneboxRestException.class, () -> {
            webhookService.sendWebhookToFever(message);
        });
    }

    @Test
    public void sendProductWebhook_nullSubtype_throwsNullPointerException() {
        WebhookFeverDTO message = generateWebhookMessage("PRODUCT");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());

        Assertions.assertThrows(NullPointerException.class, () -> {
            webhookService.sendWebhookToFever(message);
        });
    }

    @Test
    public void sendProductChannelDeletedNotification_withChannelId_ok() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_CHANNEL_DELETED");
        message.getNotificationMessage().setChannelId(123L);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());
        when(productWebhookService.sendProductChannelsUpdate(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductChannelsUpdate(any());
    }

    @Test
    public void sendProductChannelDeletedNotification_withoutChannelId_notAllowed() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_CHANNEL_DELETED");
        message.getNotificationMessage().setChannelId(null);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO());

        WebhookFeverDTO disallowedMessage = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_CHANNEL_DELETED");
        disallowedMessage.setAllowSend(false);
        when(productWebhookService.sendProductChannelsUpdate(any())).thenReturn(disallowedMessage);

        webhookService.sendWebhookToFever(message);

        verify(productWebhookService, times(1)).sendProductChannelsUpdate(any());
        verify(webhookDatasource, times(0)).sendFeverMessage(any(), any(), any());
    }

    @Test
    public void sendProductEventsNotification_withProductChannels_validationCalled() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_EVENTS");
        message.setAllowSend(true); // Simulate validation passed

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO()); // This will set allowSend to false
        when(productWebhookService.sendProductEvents(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(msEventRepository, times(1)).getProductChannels(any());
        verify(productWebhookService, times(1)).sendProductEvents(any());
    }

    @Test
    public void sendProductSessionsNotification_withProductChannels_validationCalled() throws JsonProcessingException {
        WebhookFeverDTO message = generateWebhookMessageWithSubtype("PRODUCT", "PRODUCT_SESSIONS");
        message.setAllowSend(true); // Simulate validation passed

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(msEventRepository.getProductChannels(any())).thenReturn(new ProductChannelsDTO()); // This will set allowSend to false
        when(productWebhookService.sendProductSessions(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(msEventRepository, times(1)).getProductChannels(any());
        verify(productWebhookService, times(1)).sendProductSessions(any());
    }

    private WebhookFeverDTO generateWebhookMessage(String eventName) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("ob-event")).thenReturn(eventName);
        when(req.getHeader("ob-hook-id")).thenReturn("test");
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId("1001");
        FeverMessageDTO message = new FeverMessageDTO();
        WebhookFeverDTO webhookNotification = new WebhookFeverDTO(notificationMessage, req, message);
        webhookNotification.setAllowSend(Boolean.TRUE);
        return webhookNotification;
    }

    private WebhookFeverDTO generateWebhookMessageWithSubtype(String eventName, String subtype) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("ob-event")).thenReturn(eventName);
        when(req.getHeader("ob-hook-id")).thenReturn("test");
        when(req.getHeader("ob-subtype")).thenReturn(subtype);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId("1001");
        notificationMessage.setEventId(2001L); // Add event ID for product sessions tests
        FeverMessageDTO message = new FeverMessageDTO();
        WebhookFeverDTO webhookNotification = new WebhookFeverDTO(notificationMessage, req, message);
        webhookNotification.setAllowSend(Boolean.TRUE);
        return webhookNotification;
    }

    @Test
    public void sendFeverKafkaMessage_ok_verifySendMessageCalled() throws Exception {
        WebhookFeverDTO message = generateWebhookMessageWithDeliveryId("ENTITY_FVZONE", "delivery-123");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(feverWebhookConfiguration.getHashKey()).thenReturn("test-hash-key");
        when(feverWebhookConfiguration.getUrl()).thenReturn("http://test-url.com");
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(message);

        webhookService.sendWebhookToFever(message);

        verify(feverKafkaProducer, times(1)).sendMessage(
                eq("delivery-123"),
                any(FeverKafkaMessage.class),
                any(List.class)
        );
    }

    @Test
    public void sendFeverKafkaMessage_ok_verifyPayloadContent() throws Exception {
        String deliveryId = "delivery-456";
        WebhookFeverDTO message = generateWebhookMessageWithDeliveryId("ENTITY_FVZONE", deliveryId);

        FeverMessageDTO feverMessage = new FeverMessageDTO();
        feverMessage.setId("event-123");
        feverMessage.setEventId(1001L);
        feverMessage.setName("Test Event Name");
        feverMessage.setEmail("test@example.com");
        feverMessage.setCode("ORDER-123");
        message.setFeverMessage(feverMessage);

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(feverWebhookConfiguration.getHashKey()).thenReturn("test-hash-key");
        when(feverWebhookConfiguration.getUrl()).thenReturn("http://test-url.com");
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(message);

        ArgumentCaptor<FeverKafkaMessage> payloadCaptor = ArgumentCaptor.forClass(FeverKafkaMessage.class);

        webhookService.sendWebhookToFever(message);

        verify(feverKafkaProducer).sendMessage(
                eq(deliveryId),
                payloadCaptor.capture(),
                any(List.class)
        );

        FeverKafkaMessage capturedPayload = payloadCaptor.getValue();
        assertNotNull(capturedPayload, "Payload should not be null");
        assertEquals(FeverConfig.WEBHOOK_NAME, capturedPayload.getPayload().getEventFqn(), "EventFqn should match expected value");
        assertEquals(deliveryId, capturedPayload.getPayload().getEventId(), "EventId should match deliveryId");
        assertNotNull(capturedPayload.getPayload().getCreatedAt(), "CreatedAt timestamp should not be null");

        assertNotNull(capturedPayload.getPayload(), "Payload JSON should not be null");
        String payloadJson = capturedPayload.getPayload().getPayload();
        Assertions.assertTrue(payloadJson.contains("event-123"), "Payload should contain the event ID");
        Assertions.assertTrue(payloadJson.contains("1001"), "Payload should contain the eventId");
        Assertions.assertTrue(payloadJson.contains("Test Event Name"), "Payload should contain the event name");
        Assertions.assertTrue(payloadJson.contains("test@example.com"), "Payload should contain the email");
        Assertions.assertTrue(payloadJson.contains("ORDER-123"), "Payload should contain the order code");
    }

    @Test
    public void sendFeverKafkaMessage_ok_verifyKafkaHeaders() throws Exception {
        WebhookFeverDTO message = generateWebhookMessageWithDeliveryId("ENTITY_FVZONE", "delivery-789");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(feverWebhookConfiguration.getHashKey()).thenReturn("test-hash-key");
        when(feverWebhookConfiguration.getUrl()).thenReturn("http://test-url.com");
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(message);

        ArgumentCaptor<List<Header>> headersCaptor = ArgumentCaptor.forClass(List.class);

        webhookService.sendWebhookToFever(message);

        verify(feverKafkaProducer).sendMessage(
                eq("delivery-789"),
                any(FeverKafkaMessage.class),
                headersCaptor.capture()
        );

        List<Header> capturedHeaders = headersCaptor.getValue();
        assertNotNull(capturedHeaders, "Headers should not be null");
        assertEquals(1, capturedHeaders.size(), "Should have exactly 1 header");
        assertEquals("id", capturedHeaders.get(0).key(), "Header key should be 'id'");
        assertEquals("delivery-789", new String(capturedHeaders.get(0).value()), "Header value should match deliveryId");
    }

    @Test
    public void sendFeverKafkaMessage_kafkaException_logsErrorAndContinues() throws Exception {
        WebhookFeverDTO message = generateWebhookMessageWithDeliveryId("ENTITY_FVZONE", "delivery-error");

        when(msNotificationRepository.getNotificationConfig(any())).thenReturn(new NotificationConfigDTO());
        when(feverWebhookConfiguration.getHashKey()).thenReturn("test-hash-key");
        when(feverWebhookConfiguration.getUrl()).thenReturn("http://test-url.com");
        when(entityWebhookService.sendEntityFvZoneData(any())).thenReturn(message);

        doThrow(new RuntimeException("Kafka connection error"))
                .when(feverKafkaProducer).sendMessage(any(), any(FeverKafkaPayload.class), any(List.class));

        webhookService.sendWebhookToFever(message);

        verify(feverKafkaProducer, times(1)).sendMessage(any(), any(FeverKafkaMessage.class), any(List.class));
        verify(webhookDatasource, times(1)).sendFeverMessage(any(), any(), any());
    }

    @Test
    public void feverKafkaPayload_jsonSerialization_format() throws Exception {
        // Expected JSON format (our target) - matches Jackson's pretty-print format
        String expectedJson = """
                {
                  "created_at" : "2025-10-27T11:48:24.692693Z",
                  "event_fqn" : "%s",
                  "event_id" : "019a257f-81f4-7821-9258-7b4da25a6823",
                  "payload" : "{\\"value\\": \\"This is the test buz event number: 1\\"}"
                }""".formatted(FeverConfig.WEBHOOK_TOPIC);

        // Create FeverKafkaPayload with realistic data and specific timestamp
        FeverKafkaPayload kafkaPayload = new FeverKafkaPayload();
        kafkaPayload.setCreatedAt(ZonedDateTime.parse("2025-10-27T11:48:24.692693Z").toString());
        kafkaPayload.setEventFqn(FeverConfig.WEBHOOK_TOPIC);
        kafkaPayload.setEventId("019a257f-81f4-7821-9258-7b4da25a6823");
        kafkaPayload.setPayload("{\"value\": \"This is the test buz event number: 1\"}");

        // Serialize to JSON
        String json = JsonMapper.jacksonMapper().writeValueAsString(kafkaPayload);

        // Verify the JSON matches the expected format exactly
        assertEquals(expectedJson.trim(), json, "Serialized JSON should match expected format with snake_case fields");
    }

    private WebhookFeverDTO generateWebhookMessageWithDeliveryId(String eventName, String deliveryId) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("ob-event")).thenReturn(eventName);
        when(req.getHeader("ob-hook-id")).thenReturn("test");
        when(req.getHeader("ob-delivery-id")).thenReturn(deliveryId);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId("1001");
        FeverMessageDTO message = new FeverMessageDTO();
        message.setId("test-fever-message");
        WebhookFeverDTO webhookNotification = new WebhookFeverDTO(notificationMessage, req, message);
        webhookNotification.setAllowSend(Boolean.TRUE);
        return webhookNotification;
    }
}
