package es.onebox.mgmt.channels.channeldomain.cors.converter;

import es.onebox.mgmt.customdomains.channeldomain.cors.converter.CorsSettingsConverter;
import es.onebox.mgmt.customdomains.channeldomain.cors.dto.CorsSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CorsSettingsConverterTest {

    @Test
    void testFromDTO() {
        // Given
        CorsSettingsDTO corsSettingsDTO = mockCorsSettingsDTO();

        // When
        ChannelCorsSettings result = CorsSettingsConverter.fromDTO(corsSettingsDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.getEnabled());
        assertNotNull(result.getAllowedOrigins());
        assertEquals(2, result.getAllowedOrigins().size());
        assertEquals("mock.com", result.getAllowedOrigins().get(0));
        assertEquals("test.com", result.getAllowedOrigins().get(1));
    }

    @Test
    void testToDTO() {
        // Given
        ChannelCorsSettings channelCorsSettings = mockChannelCorsSettings();

        // When
        CorsSettingsDTO result = CorsSettingsConverter.toDTO(channelCorsSettings);

        // Then
        assertNotNull(result);
        assertFalse(result.getEnabled());
        assertNotNull(result.getAllowedOrigins());
        assertEquals(2, result.getAllowedOrigins().size());
        assertEquals("test.com", result.getAllowedOrigins().get(0));
        assertEquals("mock.com", result.getAllowedOrigins().get(1));
    }

    private static ChannelCorsSettings mockChannelCorsSettings() {
        ChannelCorsSettings channelCorsSettings = new ChannelCorsSettings();
        channelCorsSettings.setEnabled(false);
        channelCorsSettings.setAllowedOrigins(List.of("test.com", "mock.com"));
        return channelCorsSettings;
    }

    private static CorsSettingsDTO mockCorsSettingsDTO() {
        CorsSettingsDTO corsSettingsDTO = new CorsSettingsDTO();
        corsSettingsDTO.setEnabled(true);
        corsSettingsDTO.setAllowedOrigins(List.of("mock.com", "test.com"));
        return corsSettingsDTO;
    }
}
