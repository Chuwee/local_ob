package es.onebox.mgmt.channels.channeldomain.cors;

import es.onebox.mgmt.customdomains.channeldomain.cors.CorsSettingsService;
import es.onebox.mgmt.customdomains.channeldomain.cors.converter.CorsSettingsConverter;
import es.onebox.mgmt.customdomains.channeldomain.cors.dto.CorsSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorsSettingsServiceTest {

    @Mock
    private ChannelsRepository channelsRepository;

    @InjectMocks
    private CorsSettingsService corsSettingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGet() {
        // Given
        Long channelId = 1L;
        ChannelCorsSettings channelCorsSettings = new ChannelCorsSettings();
        channelCorsSettings.setEnabled(true);
        channelCorsSettings.setAllowedOrigins(List.of("mock.com", "test.com"));

        when(channelsRepository.getChannelCorsSettings(channelId)).thenReturn(channelCorsSettings);

        // When
        CorsSettingsDTO result = corsSettingsService.get(channelId);

        // Then
        assertNotNull(result);
        assertTrue(result.getEnabled());
        assertNotNull(result.getAllowedOrigins());
        assertEquals(2, result.getAllowedOrigins().size());
        assertEquals("mock.com", result.getAllowedOrigins().get(0));
        assertEquals("test.com", result.getAllowedOrigins().get(1));
    }

    @Test
    void testGet_NullResult() {
        // Given
        Long channelId = 1L;
        when(channelsRepository.getChannelCorsSettings(channelId)).thenReturn(null);

        // When
        CorsSettingsDTO result = corsSettingsService.get(channelId);

        // Then
        assertNull(result);
        verify(channelsRepository, times(1)).getChannelCorsSettings(channelId);
    }

    @Test
    void testUpsert() {
        // Given
        Long channelId = 1L;
        CorsSettingsDTO corsSettingsDTO = new CorsSettingsDTO();
        corsSettingsDTO.setEnabled(false);
        corsSettingsDTO.setAllowedOrigins(List.of("mock.com", "test.com"));

        ChannelCorsSettings expectedChannelCorsSettings = CorsSettingsConverter.fromDTO(corsSettingsDTO);

        // When
        corsSettingsService.upsert(channelId, corsSettingsDTO);

        // Then
        ArgumentCaptor<ChannelCorsSettings> captor = ArgumentCaptor.forClass(ChannelCorsSettings.class);
        verify(channelsRepository, times(1)).upsertChannelCorsSettings(
                any(),
                captor.capture()
        );
        ChannelCorsSettings capturedChannelCorsSettings = captor.getValue();

        assertNotNull(capturedChannelCorsSettings);
        assertFalse(capturedChannelCorsSettings.getEnabled());
        assertEquals(expectedChannelCorsSettings.getAllowedOrigins(), capturedChannelCorsSettings.getAllowedOrigins());
        assertEquals(expectedChannelCorsSettings.getAllowedOrigins().get(0), capturedChannelCorsSettings.getAllowedOrigins().get(0));
        assertEquals(expectedChannelCorsSettings.getAllowedOrigins().get(1), capturedChannelCorsSettings.getAllowedOrigins().get(1));
    }

    @Test
    void testDisable() {
        // Given
        Long channelId = 1L;

        // When
        corsSettingsService.disable(channelId);

        // Then
        verify(channelsRepository, times(1)).disableChannelCorsSettings(channelId);
    }
}
