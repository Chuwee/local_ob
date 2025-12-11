package es.onebox.mgmt.channels.converter;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.dto.ChannelSettingsUpdateDTO;
import es.onebox.mgmt.channels.dto.DestinationChannelDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.Language;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChannelConverterTest {

    private Map<Long, String> testLanguages;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testLanguages = new HashMap<>();
        testLanguages.put(1L, "es-ES");
        testLanguages.put(2L, "en-US");
    }

    @Test
    void fromUpdateChannelRequestDTO_withDestinationChannel_shouldMapToChannelUpdateRequest() {
        // Given
        UpdateChannelRequestDTO source = new UpdateChannelRequestDTO();
        ChannelSettingsUpdateDTO settings = new ChannelSettingsUpdateDTO();
        DestinationChannelDTO destinationChannel = new DestinationChannelDTO();
        destinationChannel.setDestinationChannelId("channel-abc");
        destinationChannel.setDestinationChannelType("WEB");
        settings.setDestinationChannel(destinationChannel);
        source.setSettings(settings);

        Map<String, Long> masterLanguages = new HashMap<>();
        masterLanguages.put("es-ES", 1L);

        // When
        ChannelUpdateRequest result = ChannelConverter.fromUpdateChannelRequestDTO(
            source, masterLanguages, new ArrayList<>()
        );

        // Then
        assertNotNull(result);
        assertTrue(result.hasDestinationChannel());
        assertEquals("channel-abc", result.getDestinationChannel());
        assertEquals("WEB", result.getDestinationChannelType());
    }

    @Test
    void fromUpdateChannelRequestDTO_withoutDestinationChannel_shouldNotSetDestinationChannelFields() {
        // Given
        UpdateChannelRequestDTO source = new UpdateChannelRequestDTO();
        ChannelSettingsUpdateDTO settings = new ChannelSettingsUpdateDTO();
        settings.setDestinationChannel(null);
        source.setSettings(settings);

        Map<String, Long> masterLanguages = new HashMap<>();

        // When
        ChannelUpdateRequest result = ChannelConverter.fromUpdateChannelRequestDTO(
            source, masterLanguages, new ArrayList<>()
        );

        // Then
        assertNotNull(result);
        assertNull(result.hasDestinationChannel());
        assertNull(result.getDestinationChannel());
        assertNull(result.getDestinationChannelType());
    }

    @Test
    void fromUpdateChannelRequestDTO_withDestinationChannelWithNullValues_shouldMapWithNulls() {
        // Given
        UpdateChannelRequestDTO source = new UpdateChannelRequestDTO();
        ChannelSettingsUpdateDTO settings = new ChannelSettingsUpdateDTO();
        DestinationChannelDTO destinationChannel = new DestinationChannelDTO();
        destinationChannel.setDestinationChannelId(null);
        destinationChannel.setDestinationChannelType(null);
        settings.setDestinationChannel(destinationChannel);
        source.setSettings(settings);

        Map<String, Long> masterLanguages = new HashMap<>();

        // When
        ChannelUpdateRequest result = ChannelConverter.fromUpdateChannelRequestDTO(
            source, masterLanguages, new ArrayList<>()
        );

        // Then
        assertNotNull(result);
        assertTrue(result.hasDestinationChannel());
        assertNull(result.getDestinationChannel());
        assertNull(result.getDestinationChannelType());
    }

    @Test
    void fromUpdateChannelRequestDTO_withDestinationChannelAndOtherSettings_shouldMapAll() {
        // Given
        UpdateChannelRequestDTO source = new UpdateChannelRequestDTO();
        ChannelSettingsUpdateDTO settings = new ChannelSettingsUpdateDTO();
        settings.setAutomaticSeatSelection(true);
        settings.setEnableB2B(false);
        settings.setUseMultiEvent(true);
        
        DestinationChannelDTO destinationChannel = new DestinationChannelDTO();
        destinationChannel.setDestinationChannelId("multi-channel-123");
        destinationChannel.setDestinationChannelType("API");
        settings.setDestinationChannel(destinationChannel);
        source.setSettings(settings);

        Map<String, Long> masterLanguages = new HashMap<>();

        // When
        ChannelUpdateRequest result = ChannelConverter.fromUpdateChannelRequestDTO(
            source, masterLanguages, new ArrayList<>()
        );

        // Then
        assertNotNull(result);
        assertTrue(result.getAutomaticSeatSelection());
        assertFalse(result.getEnableB2B());
        assertTrue(result.getUseMultiEvent());
        assertTrue(result.hasDestinationChannel());
        assertEquals("multi-channel-123", result.getDestinationChannel());
        assertEquals("API", result.getDestinationChannelType());
    }

    private ChannelResponse createTestChannelResponse() {
        ChannelResponse channel = new ChannelResponse();
        channel.setId(1L);
        channel.setName("Test Channel");
        channel.setEntityName("Test Entity");
        channel.setEntityId(1L);
        channel.setDomain("test.com");
        channel.setUrl("http://test.com");
        
        channel.setStatus(ChannelStatus.ACTIVE);
        channel.setSubtype(ChannelSubtype.PORTAL_WEB);
        
        Language language = new Language();
        language.setDefaultLanguageId(1L);
        language.setSelectedLanguages(new ArrayList<>());
        channel.setLanguages(language);
        
        // Set minimal required fields to avoid NPEs
        channel.setIdReceiptTemplate(1); // Required to avoid NPE in fillSettings
        channel.setAutomaticSeatSelection(false);
        channel.setEnableB2B(false);
        channel.setUseMultiEvent(false);
        channel.setAllowDataProtectionFields(false);
        channel.setAllowLinkedCustomers(false);
        channel.setUseRobotIndexation(false);
        channel.setAllowB2BPublishing(false);
        channel.setEnableB2BEventCategoryFilter(false);
        channel.setChannelPublic(true);
        return channel;
    }
}

