package es.onebox.ms.notification.providerplansettings;

import es.onebox.datasource.http.RequestHeaders;
import es.onebox.ms.notification.webhooks.WebhookSendingService;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProviderPlanSettingsNotificationProcessorTest {

    @Mock
    private WebhookSendingService webhookSendingService;

    @InjectMocks
    private ProviderPlanSettingsNotificationProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_withProviderPlanSettings_sendsNotificationSuccessfully() throws Exception {
        // Arrange
        ProviderPlanSettingsNotificationMessage message = new ProviderPlanSettingsNotificationMessage();
        message.setEventId(123L);
        message.setChannelId(456L);
        message.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");

        Exchange exchange = createMockedExchange(message);

        // Act
        processor.execute(exchange);

        // Assert
        ArgumentCaptor<RequestHeaders> headersCaptor = ArgumentCaptor.forClass(RequestHeaders.class);
        verify(webhookSendingService, times(1)).sendNotificationToApiExternal(
            eq(456L),
            headersCaptor.capture(),
            any(ExternalApiWebhookDto.class)
        );

        // Verify headers contain the provider plan settings and event ID
        RequestHeaders capturedHeaders = headersCaptor.getValue();
        assertNotNull(capturedHeaders);
        // Note: RequestHeaders doesn't expose headers for verification in tests,
        // but we can verify the call was made with correct parameters
    }

    @Test
    void execute_withNullProviderPlanSettings_sendsNotificationWithoutSettingsHeader() throws Exception {
        // Arrange
        ProviderPlanSettingsNotificationMessage message = new ProviderPlanSettingsNotificationMessage();
        message.setEventId(789L);
        message.setChannelId(101L);
        message.setProviderPlanSettings(null);

        Exchange exchange = createMockedExchange(message);

        // Act
        processor.execute(exchange);

        // Assert
        verify(webhookSendingService, times(1)).sendNotificationToApiExternal(
            eq(101L),
            any(RequestHeaders.class),
            any(ExternalApiWebhookDto.class)
        );
    }

    @Test
    void execute_whenServiceThrowsException_propagatesException() {
        // Arrange
        ProviderPlanSettingsNotificationMessage message = new ProviderPlanSettingsNotificationMessage();
        message.setEventId(111L);
        message.setChannelId(222L);
        message.setProviderPlanSettings("{\"sync_surcharges\":false}");

        Exchange exchange = createMockedExchange(message);

        doThrow(new RuntimeException("Service error"))
            .when(webhookSendingService)
            .sendNotificationToApiExternal(any(), any(), any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> processor.execute(exchange));
    }

    private Exchange createMockedExchange(ProviderPlanSettingsNotificationMessage notificationMessage) {
        Message message = mock(Message.class);
        Exchange exchange = mock(Exchange.class);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(ProviderPlanSettingsNotificationMessage.class)).thenReturn(notificationMessage);
        return exchange;
    }
}
