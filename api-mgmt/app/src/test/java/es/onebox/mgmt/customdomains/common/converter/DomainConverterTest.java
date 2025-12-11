package es.onebox.mgmt.customdomains.common.converter;

import es.onebox.mgmt.customdomains.common.converter.DomainSettingsConverter;
import es.onebox.mgmt.customdomains.common.dto.CustomDomainSetting;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsMode;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.customdomains.common.dto.CustomDomain;
import es.onebox.mgmt.customdomains.common.dto.DomainMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainConverterTest {

    @Test
    void testFromDTO() {
        // Given
        DomainSettingsDTO domainChannelsSettingsDTO = mockDomainSettingsDTO();

        // When
        DomainSettings result = DomainSettingsConverter.fromDTO(domainChannelsSettingsDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.getEnabled());
        assertEquals(DomainMode.DEFAULT, result.getMode());
        assertNotNull(result.getDomains());
        assertEquals(1, result.getDomains().size());
        assertEquals("mock.com", result.getDomains().get(0).getDomain());
        assertTrue(result.getDomains().get(0).getDefaultDomain());
    }

    @Test
    void testToDTO() {
        // Given
        DomainSettings channelDomainSettings = mockChannelDomainSettings();

        // When
        DomainSettingsDTO result = DomainSettingsConverter.toDTO(channelDomainSettings);

        // Then
        assertNotNull(result);
        assertFalse(result.getEnabled());
        assertEquals(DomainSettingsMode.DEFAULT, result.getMode());
        assertNotNull(result.getDomains());
        assertEquals(1, result.getDomains().size());
        assertEquals("test.com", result.getDomains().get(0).getDomain());
        assertFalse(result.getDomains().get(0).getDefaultDomain());
    }

    private static DomainSettings mockChannelDomainSettings() {
        DomainSettings channelDomainSettings = new DomainSettings();
        channelDomainSettings.setEnabled(false);
        channelDomainSettings.setMode(DomainMode.DEFAULT);
        channelDomainSettings.setDomains(List.of(new CustomDomain("test.com", false)));
        return channelDomainSettings;
    }

    private static DomainSettingsDTO mockDomainSettingsDTO() {
        DomainSettingsDTO domainChannelsSettingsDTO = new DomainSettingsDTO();
        domainChannelsSettingsDTO.setEnabled(true);
        domainChannelsSettingsDTO.setMode(DomainSettingsMode.DEFAULT);
        domainChannelsSettingsDTO.setDomains(List.of(new CustomDomainSetting("mock.com", true)));
        return domainChannelsSettingsDTO;
    }

}
