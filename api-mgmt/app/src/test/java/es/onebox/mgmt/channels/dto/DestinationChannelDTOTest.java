package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DestinationChannelDTOTest {

    private DestinationChannelDTO destinationChannelDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        destinationChannelDTO = new DestinationChannelDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGettersAndSetters() {
        String channelType = "WEB";
        String channelId = "12345";

        destinationChannelDTO.setDestinationChannelType(channelType);
        destinationChannelDTO.setDestinationChannelId(channelId);

        assertEquals(channelType, destinationChannelDTO.getDestinationChannelType());
        assertEquals(channelId, destinationChannelDTO.getDestinationChannelId());
    }

    @Test
    void testSetDestinationChannelType_withNull() {
        destinationChannelDTO.setDestinationChannelType(null);
        assertNull(destinationChannelDTO.getDestinationChannelType());
    }

    @Test
    void testSetDestinationChannelId_withNull() {
        destinationChannelDTO.setDestinationChannelId(null);
        assertNull(destinationChannelDTO.getDestinationChannelId());
    }

    @Test
    void testSetDestinationChannelType_withEmptyString() {
        destinationChannelDTO.setDestinationChannelType("");
        assertEquals("", destinationChannelDTO.getDestinationChannelType());
    }

    @Test
    void testSetDestinationChannelId_withEmptyString() {
        destinationChannelDTO.setDestinationChannelId("");
        assertEquals("", destinationChannelDTO.getDestinationChannelId());
    }

    @Test
    void testSerializable() {
        destinationChannelDTO.setDestinationChannelType("MOBILE");
        destinationChannelDTO.setDestinationChannelId("67890");

        assertDoesNotThrow(() -> {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(destinationChannelDTO);
            oos.close();

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            DestinationChannelDTO deserialized = (DestinationChannelDTO) ois.readObject();
            ois.close();

            assertEquals("MOBILE", deserialized.getDestinationChannelType());
            assertEquals("67890", deserialized.getDestinationChannelId());
        });
    }

    @Test
    void testJsonSerialization() throws Exception {
        destinationChannelDTO.setDestinationChannelType("API");
        destinationChannelDTO.setDestinationChannelId("99999");

        String json = objectMapper.writeValueAsString(destinationChannelDTO);
        
        assertTrue(json.contains("destination_channel_type"));
        assertTrue(json.contains("destination_channel_id"));
        assertTrue(json.contains("API"));
        assertTrue(json.contains("99999"));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{\"destination_channel_type\":\"WEB\",\"destination_channel_id\":\"11111\"}";
        
        DestinationChannelDTO result = objectMapper.readValue(json, DestinationChannelDTO.class);
        
        assertNotNull(result);
        assertEquals("WEB", result.getDestinationChannelType());
        assertEquals("11111", result.getDestinationChannelId());
    }

    @Test
    void testJsonDeserialization_withNullValues() throws Exception {
        String json = "{\"destination_channel_type\":null,\"destination_channel_id\":null}";
        
        DestinationChannelDTO result = objectMapper.readValue(json, DestinationChannelDTO.class);
        
        assertNotNull(result);
        assertNull(result.getDestinationChannelType());
        assertNull(result.getDestinationChannelId());
    }

    @Test
    void testJsonDeserialization_withMissingFields() throws Exception {
        String json = "{}";
        
        DestinationChannelDTO result = objectMapper.readValue(json, DestinationChannelDTO.class);
        
        assertNotNull(result);
        assertNull(result.getDestinationChannelType());
        assertNull(result.getDestinationChannelId());
    }

    @Test
    void testInitialStateIsNull() {
        DestinationChannelDTO newDTO = new DestinationChannelDTO();
        
        assertNull(newDTO.getDestinationChannelType());
        assertNull(newDTO.getDestinationChannelId());
    }
}

