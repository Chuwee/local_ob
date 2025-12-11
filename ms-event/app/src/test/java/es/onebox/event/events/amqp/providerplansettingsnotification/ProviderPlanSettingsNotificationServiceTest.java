package es.onebox.event.events.amqp.providerplansettingsnotification;

import es.onebox.event.events.dto.ProviderPlanSettings;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderPlanSettingsNotificationServiceTest {

    @Mock
    private DefaultProducer providerPlanSettingsNotificationProducer;

    @InjectMocks
    private ProviderPlanSettingsNotificationService service;

    private ProviderPlanSettings testSettings;
    private Long eventId;
    private Long channelId;

    @BeforeEach
    void setUp() {
        eventId = 123L;
        channelId = 456L;
        
        testSettings = new ProviderPlanSettings();
        testSettings.setSyncSessionsAsHidden(true);
        testSettings.setSyncSurcharges(false);
        testSettings.setSyncSessionLabels(true);
    }

    @Test
    void testSendProviderPlanSettingsNotification_Success() throws Exception {
        // When
        service.sendProviderPlanSettingsNotification(eventId, channelId, testSettings);

        // Then
        ArgumentCaptor<ProviderPlanSettingsNotificationMessage> messageCaptor = 
            ArgumentCaptor.forClass(ProviderPlanSettingsNotificationMessage.class);
        verify(providerPlanSettingsNotificationProducer).sendMessage(messageCaptor.capture());

        ProviderPlanSettingsNotificationMessage capturedMessage = messageCaptor.getValue();
        assertEquals(eventId, capturedMessage.getEventId());
        assertEquals(channelId, capturedMessage.getChannelId());
        assertNotNull(capturedMessage.getProviderPlanSettings());
        assertTrue(capturedMessage.getProviderPlanSettings().contains("sync_sessions_as_hidden"));
    }

    @Test
    void testSendProviderPlanSettingsNotification_NullSettings() throws Exception {
        // When
        service.sendProviderPlanSettingsNotification(eventId, channelId, null);

        // Then
        verify(providerPlanSettingsNotificationProducer, never()).sendMessage(any());
    }

    @Test
    void testSendProviderPlanSettingsNotification_ProducerException() throws Exception {
        // Given
        doThrow(new RuntimeException("Queue error")).when(providerPlanSettingsNotificationProducer)
            .sendMessage(any(ProviderPlanSettingsNotificationMessage.class));

        // When - should not throw exception
        assertDoesNotThrow(() -> 
            service.sendProviderPlanSettingsNotification(eventId, channelId, testSettings)
        );

        // Then - verify the message was attempted to be sent
        verify(providerPlanSettingsNotificationProducer).sendMessage(any(ProviderPlanSettingsNotificationMessage.class));
    }
}
