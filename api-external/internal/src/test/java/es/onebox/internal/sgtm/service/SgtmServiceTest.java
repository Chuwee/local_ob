package es.onebox.internal.sgtm.service;

import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SgtmServiceTest {

    @Mock
    private ProviderPlanSettingsService providerPlanSettingsService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private SgtmService sgtmService;

    @BeforeEach
    void setUp() {
        // Setup common mock behavior
        when(httpServletRequest.getHeader("ob-action")).thenReturn("PROVIDER_PLAN_SETTINGS_UPDATE");
        when(httpServletRequest.getHeader("ob-event")).thenReturn("EVENT_CHANNEL");
        when(httpServletRequest.getHeader("ob-delivery-id")).thenReturn("delivery-123");
        when(httpServletRequest.getHeader("ob-hook-id")).thenReturn("hook-456");
        when(httpServletRequest.getHeader("ob-signature")).thenReturn("sig-789");
        when(httpServletRequest.getHeader("x-gtm-server-preview")).thenReturn(null);
    }

    @Test
    void processWebhook_withProviderPlanSettings_shouldCallService() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");
        List<Long> channelIds = List.of(456L);

        doNothing().when(providerPlanSettingsService)
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());

        // Act
        assertDoesNotThrow(() -> sgtmService.processWebhook(request, httpServletRequest, channelIds));

        // Assert
        verify(providerPlanSettingsService, times(1))
                .sendProviderPlanSettingsToFever(123L, 456L, "{\"sync_sessions_as_hidden\":true}");
    }

    @Test
    void processWebhook_withMultipleChannels_shouldCallServiceForEach() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");
        List<Long> channelIds = List.of(456L, 789L);

        doNothing().when(providerPlanSettingsService)
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());

        // Act
        assertDoesNotThrow(() -> sgtmService.processWebhook(request, httpServletRequest, channelIds));

        // Assert
        verify(providerPlanSettingsService, times(1))
                .sendProviderPlanSettingsToFever(123L, 456L, "{\"sync_sessions_as_hidden\":true}");
        verify(providerPlanSettingsService, times(1))
                .sendProviderPlanSettingsToFever(123L, 789L, "{\"sync_sessions_as_hidden\":true}");
    }

    @Test
    void processWebhook_withNullProviderPlanSettings_shouldStillProcess() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings(null);
        List<Long> channelIds = List.of(456L);

        doNothing().when(providerPlanSettingsService)
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), isNull());

        // Act
        assertDoesNotThrow(() -> sgtmService.processWebhook(request, httpServletRequest, channelIds));

        // Assert
        verify(providerPlanSettingsService, times(1))
                .sendProviderPlanSettingsToFever(123L, 456L, null);
    }

    @Test
    void processWebhook_withMissingEventId_shouldThrow() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(null);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");
        List<Long> channelIds = List.of(456L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sgtmService.processWebhook(request, httpServletRequest, channelIds));
        
        assertTrue(exception.getMessage().contains("Event ID is required"));
        verify(providerPlanSettingsService, never())
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());
    }

    @Test
    void processWebhook_withEmptyChannelIds_shouldThrow() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");
        List<Long> channelIds = Collections.emptyList();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sgtmService.processWebhook(request, httpServletRequest, channelIds));
        
        assertTrue(exception.getMessage().contains("At least one channel ID is required"));
        verify(providerPlanSettingsService, never())
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());
    }

    @Test
    void processWebhook_withNullChannelIds_shouldThrow() {
        // Arrange
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sgtmService.processWebhook(request, httpServletRequest, null));
        
        assertTrue(exception.getMessage().contains("At least one channel ID is required"));
        verify(providerPlanSettingsService, never())
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());
    }

    @Test
    void processWebhook_withDifferentAction_shouldNotCallService() {
        // Arrange
        when(httpServletRequest.getHeader("ob-action")).thenReturn("SOME_OTHER_ACTION");
        
        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEventId(123L);
        request.setProviderPlanSettings("{\"sync_sessions_as_hidden\":true}");
        List<Long> channelIds = List.of(456L);

        // Act
        assertDoesNotThrow(() -> sgtmService.processWebhook(request, httpServletRequest, channelIds));

        // Assert
        verify(providerPlanSettingsService, never())
                .sendProviderPlanSettingsToFever(anyLong(), anyLong(), anyString());
    }
}
