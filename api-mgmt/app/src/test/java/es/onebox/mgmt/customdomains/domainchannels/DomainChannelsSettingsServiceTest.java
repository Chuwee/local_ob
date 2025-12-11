package es.onebox.mgmt.customdomains.domainchannels;

import es.onebox.mgmt.customdomains.channeldomain.domainchannels.DomainChannelsSettingsService;
import es.onebox.mgmt.customdomains.common.converter.DomainSettingsConverter;
import es.onebox.mgmt.customdomains.common.dto.CustomDomainSetting;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsMode;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.customdomains.common.dto.CustomDomain;
import es.onebox.mgmt.customdomains.common.dto.DomainMode;
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

class DomainChannelsSettingsServiceTest {

    @Mock
    private ChannelsRepository channelsRepository;

    @InjectMocks
    private DomainChannelsSettingsService domainChannelsSettingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGet() {
        // Given
        Long channelId = 1L;
        DomainSettings channelDomainSettings = new DomainSettings();
        channelDomainSettings.setEnabled(true);
        channelDomainSettings.setMode(DomainMode.DEFAULT);
        channelDomainSettings.setDomains(List.of(new CustomDomain("example.com", false)));

        when(channelsRepository.getChannelDomainSettings(channelId)).thenReturn(channelDomainSettings);

        // When
        DomainSettingsDTO result = domainChannelsSettingsService.get(channelId);

        // Then
        assertNotNull(result);
        assertTrue(result.getEnabled());
        assertEquals(DomainSettingsMode.DEFAULT, result.getMode());
        assertNotNull(result.getDomains());
        assertEquals(1, result.getDomains().size());
        assertEquals("example.com", result.getDomains().get(0).getDomain());
        assertFalse(result.getDomains().get(0).getDefaultDomain());
        verify(channelsRepository, times(1)).getChannelDomainSettings(channelId);
    }

    @Test
    void testGet_NullResult() {
        // Given
        Long channelId = 2L;
        when(channelsRepository.getChannelDomainSettings(channelId)).thenReturn(null);

        // When
        DomainSettingsDTO result = domainChannelsSettingsService.get(channelId);

        // Then
        assertNull(result);
        verify(channelsRepository, times(1)).getChannelDomainSettings(channelId);
    }

    @Test
    void testUpsert() {
        // Given
        Long channelId = 3L;
        DomainSettingsDTO dto = new DomainSettingsDTO();
        dto.setEnabled(true);
        dto.setMode(DomainSettingsMode.DEFAULT);
        dto.setDomains(List.of(new CustomDomainSetting("example-upsert.com", true)));

        DomainSettings expectedChannelDomainSettings = DomainSettingsConverter.fromDTO(dto);

        // When
        domainChannelsSettingsService.upsert(channelId, dto);

        // Then
        ArgumentCaptor<DomainSettings> captor = ArgumentCaptor.forClass(DomainSettings.class);
        verify(channelsRepository, times(1)).upsertChannelDomainSettings(
                any(),
                captor.capture()
        );

        DomainSettings capturedChannelDomainSettings = captor.getValue();

        assertNotNull(capturedChannelDomainSettings);
        assertTrue(capturedChannelDomainSettings.getEnabled());
        assertEquals(expectedChannelDomainSettings.getMode(), capturedChannelDomainSettings.getMode());
        assertNotNull(capturedChannelDomainSettings.getDomains());
        assertEquals(1, capturedChannelDomainSettings.getDomains().size());
        assertEquals("example-upsert.com", capturedChannelDomainSettings.getDomains().get(0).getDomain());
        assertTrue(capturedChannelDomainSettings.getDomains().get(0).getDefaultDomain());
        verify(channelsRepository, times(1)).upsertChannelDomainSettings(any(), any());
    }

    @Test
    void testDisable() {
        // Given
        Long channelId = 4L;

        // When
        domainChannelsSettingsService.disable(channelId);

        // Then
        verify(channelsRepository, times(1)).disableChannelDomainSettings(channelId);
    }

}
