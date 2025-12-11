package es.onebox.mgmt.entities.converter;

import es.onebox.core.security.EntityTypes;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityStatus;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.enums.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityConverterTest {

    private Entity testEntity;
    private Map<Long, String> testLanguages;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEntity = createTestEntity();
        testLanguages = createTestLanguages();
    }

    @Test
    void fromMsEntity_withBasicEntity_shouldConvertCorrectly() {
        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages);

        assertNotNull(result);
        assertEquals(testEntity.getId(), result.getId());
        assertEquals(testEntity.getName(), result.getName());
        assertEquals(testEntity.getShortName(), result.getShortName());
        assertEquals(testEntity.getReference(), result.getReference());
        assertEquals(testEntity.getNif(), result.getNif());
    }

    @Test
    void fromMsEntity_withAllBooleanSettings_shouldMapCorrectly() {
        // Given
        testEntity.setAllowPngConversion(true);
        testEntity.setAllowFriends(true);
        testEntity.setAllowLoyaltyPoints(true);
        testEntity.setAllowMembers(true);
        testEntity.setAllowVipViews(true);
        testEntity.setAllowDataProtectionFields(true);
        testEntity.setUseMultieventCart(true);
        testEntity.setModuleB2BEnabled(true);
        testEntity.setAllowB2BPublishing(true);
        testEntity.setAllowInvitations(true);
        testEntity.setUseSecondaryMarket(true);

        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        assertNotNull(result);
        EntitySettingsDTO settings = result.getSettings();
        assertNotNull(settings);
        
        assertTrue(settings.getAllowPngConversion());
        assertTrue(settings.getAllowFriends());
        assertTrue(settings.getAllowLoyaltyPoints());
        assertTrue(settings.getAllowMembers());
        assertTrue(settings.getAllowVipViews());
        assertTrue(settings.getAllowDataProtectionFields());
        assertTrue(settings.getEnableMultieventCart());
        assertTrue(settings.getEnableB2B());
        assertTrue(settings.getAllowB2BPublishing());
        assertTrue(settings.getAllowInvitations());
        assertTrue(settings.getAllowSecondaryMarket());
    }

    @Test
    void fromMsEntity_withNullBooleanSettings_shouldMapToFalse() {
        testEntity.setAllowPngConversion(null);
        testEntity.setAllowFriends(null);
        testEntity.setAllowLoyaltyPoints(null);
        testEntity.setAllowMembers(null);
        testEntity.setAllowVipViews(null);
        testEntity.setAllowDataProtectionFields(null);
        testEntity.setUseMultieventCart(null);
        testEntity.setModuleB2BEnabled(null);
        testEntity.setAllowB2BPublishing(null);
        testEntity.setAllowInvitations(null);
        testEntity.setUseSecondaryMarket(null);

        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        assertNotNull(result);
        EntitySettingsDTO settings = result.getSettings();
        assertNotNull(settings);
        
        // Test that BooleanUtils.isTrue() converts null to false
        assertFalse(settings.getAllowPngConversion());
        assertFalse(settings.getAllowFriends());
        assertFalse(settings.getAllowLoyaltyPoints());
        
        // These don't use BooleanUtils.isTrue(), so they should be null
        assertNull(settings.getAllowMembers());
        assertNull(settings.getAllowVipViews());
        assertNull(settings.getAllowDataProtectionFields());
        assertNull(settings.getEnableMultieventCart());
        assertNull(settings.getEnableB2B());
        assertNull(settings.getAllowB2BPublishing());
        assertNull(settings.getAllowInvitations());
        assertNull(settings.getAllowSecondaryMarket());
    }

    @Test
    void fromMsEntity_withEntityTypes_shouldConvertCorrectly() {
        testEntity.setTypes(Arrays.asList(EntityTypes.CHANNEL_ENTITY, EntityTypes.ENTITY_ADMIN));

        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertNotNull(result.getSettings().getTypes());
        assertEquals(2, result.getSettings().getTypes().size());
        assertTrue(result.getSettings().getTypes().contains(EntityType.CHANNEL_ENTITY));
        assertTrue(result.getSettings().getTypes().contains(EntityType.ENTITY_ADMIN));
    }

    @Test
    void fromMsEntity_withLanguages_shouldMapLanguageSettings() {
        testEntity.setLanguage(new IdValueCodeDTO(1L));
        testEntity.setSelectedLanguages(Arrays.asList(
            new IdValueCodeDTO(1L),
            new IdValueCodeDTO(2L)
        ));

        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertNotNull(result.getSettings().getLanguages());
        assertEquals("es-ES", result.getSettings().getLanguages().getDefaultLanguage());
        assertEquals(2, result.getSettings().getLanguages().getAvailableLanguages().size());
        assertTrue(result.getSettings().getLanguages().getAvailableLanguages().contains("es-ES"));
        assertTrue(result.getSettings().getLanguages().getAvailableLanguages().contains("en-US"));
    }

    @Test
    void fromMsEntity_withNullEntity_shouldReturnNull() {
        EntityDTO result = EntityConverter.fromMsEntity(null, testLanguages);
        assertNull(result);
    }

    @Test
    void fromMsEntity_withExternalIntegration_shouldMapCorrectly() {
        AuthVendorEntityConfig authConfig = new AuthVendorEntityConfig();
        authConfig.setAllowed(true);
        authConfig.setVendors(Arrays.asList("vendor1"));

        PhoneValidatorEntityConfig phoneConfig = new PhoneValidatorEntityConfig();
        phoneConfig.setEnabled(true);
        phoneConfig.setValidatorId("validator1");

        ExternalBarcodeEntityConfig barcodeConfig = new ExternalBarcodeEntityConfig();
        barcodeConfig.setAllowExternalBarcode(true);
        barcodeConfig.setExternalBarcodeFormatId("format1");

        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            authConfig, phoneConfig, barcodeConfig, true, null, null);

        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertNotNull(result.getSettings().getExternalIntegration());
        assertNotNull(result.getSettings().getExternalIntegration().getAuthVendor());
        assertTrue(result.getSettings().getExternalIntegration().getAuthVendor().getEnabled());
        assertNotNull(result.getSettings().getExternalIntegration().getPhoneValidator());
        assertTrue(result.getSettings().getExternalIntegration().getPhoneValidator().getEnabled());
        assertNotNull(result.getSettings().getExternalIntegration().getExternalBarcode());
        assertTrue(result.getSettings().getExternalIntegration().getExternalBarcode().getEnabled());
    }

    @Test
    void convertEntityTypes_shouldHandleMultiProducerConversion() {
        EntityType result = EntityConverter.convertEntityTypes(EntityTypes.MULTI_PROMOTER);
        assertEquals(EntityType.MULTI_PRODUCER, result);
    }

    @Test
    void convertEntityTypes_shouldHandleStandardTypes() {
        EntityType result = EntityConverter.convertEntityTypes(EntityTypes.CHANNEL_ENTITY);
        assertEquals(EntityType.CHANNEL_ENTITY, result);
    }

    @Test
    void convertEntityType_shouldHandleMultiProducerConversion() {
        EntityTypes result = EntityConverter.convertEntityType(EntityType.MULTI_PRODUCER);
        assertEquals(EntityTypes.MULTI_PROMOTER, result);
    }

    @Test
    void convertEntityType_shouldHandleStandardTypes() {
        EntityTypes result = EntityConverter.convertEntityType(EntityType.CHANNEL_ENTITY);
        assertEquals(EntityTypes.CHANNEL_ENTITY, result);
    }

    @Test
    void fromMsEntity_withAllowDestinationChannelsTrue_shouldMapCorrectly() {
        // Given
        testEntity.setAllowDestinationChannels(true);

        // When
        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertTrue(result.getSettings().getAllowDestinationChannels());
    }

    @Test
    void fromMsEntity_withAllowDestinationChannelsFalse_shouldMapCorrectly() {
        // Given
        testEntity.setAllowDestinationChannels(false);

        // When
        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertFalse(result.getSettings().getAllowDestinationChannels());
    }

    @Test
    void fromMsEntity_withAllowDestinationChannelsNull_shouldMapCorrectly() {
        // Given
        testEntity.setAllowDestinationChannels(null);

        // When
        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertNull(result.getSettings().getAllowDestinationChannels());
    }

    @Test
    void fromMsEntity_withAllowDestinationChannelsAndOtherSettings_shouldMapAllCorrectly() {
        // Given
        testEntity.setAllowDestinationChannels(true);
        testEntity.setAllowPngConversion(true);
        testEntity.setAllowFriends(true);
        testEntity.setAllowLoyaltyPoints(true);

        // When
        EntityDTO result = EntityConverter.fromMsEntity(testEntity, testLanguages, 
            null, null, null, true, null, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSettings());
        assertTrue(result.getSettings().getAllowDestinationChannels());
        assertTrue(result.getSettings().getAllowPngConversion());
        assertTrue(result.getSettings().getAllowFriends());
        assertTrue(result.getSettings().getAllowLoyaltyPoints());
    }

    private Entity createTestEntity() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("Test Entity");
        entity.setShortName("TEST");
        entity.setReference("REF001");
        entity.setNif("12345678A");
        entity.setSocialReason("Test Social Reason");
        entity.setState(EntityStatus.ACTIVE);
        entity.setTypes(Arrays.asList(EntityTypes.CHANNEL_ENTITY));
        entity.setLanguage(new IdValueCodeDTO(1L));
        
        entity.setAllowPngConversion(false);
        entity.setAllowFriends(false);
        entity.setAllowLoyaltyPoints(false);
        entity.setAllowMembers(false);
        entity.setAllowVipViews(false);
        entity.setAllowDataProtectionFields(false);
        entity.setUseMultieventCart(false);
        entity.setModuleB2BEnabled(false);
        entity.setAllowB2BPublishing(false);
        entity.setAllowInvitations(false);
        entity.setUseSecondaryMarket(false);
        
        return entity;
    }

    private Map<Long, String> createTestLanguages() {
        Map<Long, String> languages = new HashMap<>();
        languages.put(1L, "es-ES");
        languages.put(2L, "en-US");
        languages.put(3L, "fr-FR");
        return languages;
    }
}
