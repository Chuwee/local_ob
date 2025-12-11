package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSaleRequestChannelFilter;
import es.onebox.mgmt.events.dto.channel.EventSaleRequestChannelFilterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventChannelConverterTest {

    private Long testEntityId;
    private List<Long> testVisibleEntities;
    private Long testOperatorId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEntityId = 100L;
        testVisibleEntities = Arrays.asList(100L, 101L, 102L);
        testOperatorId = 50L;
    }

    @Test
    void toMs_withDestinationChannelType_shouldMapCorrectly() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setName("Test Channel");
        filterDTO.setIncludeThirdPartyChannels(true);
        filterDTO.setDestinationChannelType("WEB");
        filterDTO.setOffset(0L);
        filterDTO.setLimit(10L);

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("WEB", result.getDestinationChannelType());
        assertEquals("Test Channel", result.getName());
        assertTrue(result.getIncludeThirdPartyChannels());
        assertEquals(testEntityId, result.getEntityId());
        assertEquals(testVisibleEntities, result.getVisibleEntities());
        assertEquals(testOperatorId, result.getOperatorId());
        assertEquals(0, result.getOffset());
        assertEquals(10, result.getLimit());
    }

    @Test
    void toMs_withoutDestinationChannelType_shouldMapWithNull() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setName("Test Channel 2");
        filterDTO.setIncludeThirdPartyChannels(false);
        filterDTO.setDestinationChannelType(null);

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertNull(result.getDestinationChannelType());
        assertEquals("Test Channel 2", result.getName());
        assertFalse(result.getIncludeThirdPartyChannels());
    }

    @Test
    void toMs_withEmptyDestinationChannelType_shouldMapEmptyString() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setName("Test Channel 3");
        filterDTO.setDestinationChannelType("");

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("", result.getDestinationChannelType());
    }

    @Test
    void toMs_withVariousDestinationChannelTypes_shouldMapCorrectly() {
        String[] channelTypes = {"WEB", "MOBILE", "API", "APP", "CUSTOM"};

        for (String channelType : channelTypes) {
            // Given
            EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
            filterDTO.setDestinationChannelType(channelType);

            // When
            EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
                filterDTO, testEntityId, testVisibleEntities, testOperatorId
            );

            // Then
            assertNotNull(result);
            assertEquals(channelType, result.getDestinationChannelType(), 
                "Failed to map channel type: " + channelType);
        }
    }

    @Test
    void toMs_withDestinationChannelTypeAndChannelSubtypes_shouldMapBoth() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("API");
        filterDTO.setType(Arrays.asList(
            ChannelSubtype.WEB,
            ChannelSubtype.EXTERNAL
        ));

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("API", result.getDestinationChannelType());
        assertNotNull(result.getType());
        assertEquals(2, result.getType().size());
    }

    @Test
    void toMs_withDestinationChannelTypeAndNullSubtypes_shouldMapDestinationChannelOnly() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("MOBILE");
        filterDTO.setType(null);

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("MOBILE", result.getDestinationChannelType());
        assertNull(result.getType());
    }

    @Test
    void toMs_withAllFieldsPopulated_shouldMapEverything() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setName("Complete Test Channel");
        filterDTO.setIncludeThirdPartyChannels(true);
        filterDTO.setDestinationChannelType("WEB");
        filterDTO.setOffset(20L);
        filterDTO.setLimit(50L);
        filterDTO.setType(Arrays.asList(ChannelSubtype.WEB));

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("Complete Test Channel", result.getName());
        assertTrue(result.getIncludeThirdPartyChannels());
        assertEquals("WEB", result.getDestinationChannelType());
        assertEquals(20, result.getOffset());
        assertEquals(50, result.getLimit());
        assertEquals(testEntityId, result.getEntityId());
        assertEquals(testVisibleEntities, result.getVisibleEntities());
        assertEquals(testOperatorId, result.getOperatorId());
        assertNotNull(result.getType());
        assertEquals(1, result.getType().size());
    }

    @Test
    void toMs_withMinimalFields_shouldMapWithDefaults() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("APP");

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("APP", result.getDestinationChannelType());
        assertEquals(testEntityId, result.getEntityId());
        assertEquals(testVisibleEntities, result.getVisibleEntities());
        assertEquals(testOperatorId, result.getOperatorId());
        assertNull(result.getName());
        assertNull(result.getIncludeThirdPartyChannels());
        assertNull(result.getType());
    }

    @Test
    void toMs_withNullEntityId_shouldMapCorrectly() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("WEB");

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, null, testVisibleEntities, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("WEB", result.getDestinationChannelType());
        assertNull(result.getEntityId());
        assertEquals(testVisibleEntities, result.getVisibleEntities());
        assertEquals(testOperatorId, result.getOperatorId());
    }

    @Test
    void toMs_withNullVisibleEntities_shouldMapCorrectly() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("MOBILE");

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, null, testOperatorId
        );

        // Then
        assertNotNull(result);
        assertEquals("MOBILE", result.getDestinationChannelType());
        assertEquals(testEntityId, result.getEntityId());
        assertNull(result.getVisibleEntities());
        assertEquals(testOperatorId, result.getOperatorId());
    }

    @Test
    void toMs_withNullOperatorId_shouldMapCorrectly() {
        // Given
        EventSaleRequestChannelFilterDTO filterDTO = new EventSaleRequestChannelFilterDTO();
        filterDTO.setDestinationChannelType("API");

        // When
        EventSaleRequestChannelFilter result = EventChannelConverter.toMs(
            filterDTO, testEntityId, testVisibleEntities, null
        );

        // Then
        assertNotNull(result);
        assertEquals("API", result.getDestinationChannelType());
        assertEquals(testEntityId, result.getEntityId());
        assertEquals(testVisibleEntities, result.getVisibleEntities());
        assertNull(result.getOperatorId());
    }
}

