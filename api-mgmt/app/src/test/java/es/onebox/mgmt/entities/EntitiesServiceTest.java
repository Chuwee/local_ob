package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.client.repositories.AuthVendorEntityRepository;
import es.onebox.mgmt.datasources.ms.client.repositories.PhoneValidatorEntityRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityExternalBarcodeConfigRepository;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EntitiesServiceTest {

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @Mock
    private EntitiesRepository entitiesRepository;
    
    @Mock
    private AuthVendorEntityRepository authVendorEntityRepository;
    
    @Mock
    private PhoneValidatorEntityRepository phoneValidatorEntityRepository;
    
    @Mock
    private EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository;
    
    @Mock
    private MasterdataService masterdataService;
    
    @Mock
    private SecurityManager securityManager;

    @InjectMocks
    private EntitiesService entitiesService;

    private Entity testEntity;
    private Map<Long, String> testLanguages;

    @BeforeAll
    public static void beforeAll() {
        securityUtilsMockedStatic = Mockito.mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        securityUtilsMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEntity = createTestEntity();
        testLanguages = createTestLanguages();
    }

    @Test
    void getEntity_withValidId_shouldReturnEntityDTO() {
        Long entityId = 1L;
        when(entitiesRepository.getEntity(entityId)).thenReturn(testEntity);
        when(masterdataService.getLanguagesByIds()).thenReturn(testLanguages);
        when(entitiesRepository.getAttributes(entityId, null)).thenReturn(Arrays.asList());

        EntityDTO result = entitiesService.getEntity(entityId);

        assertNotNull(result);
        assertEquals(entityId, result.getId());
        assertEquals("Test Entity", result.getName());
        verify(securityManager).checkEntityAccessibleIncludeEntityAdmin(entityId);
    }

    @Test
    void getEntity_withDeletedEntity_shouldThrowException() {
        Long entityId = 1L;
        testEntity.setState(EntityStatus.DELETED);
        when(entitiesRepository.getEntity(entityId)).thenReturn(testEntity);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.getEntity(entityId));
        assertEquals(ApiMgmtErrorCode.ENTITY_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void getEntity_withNonExistentId_shouldThrowException() {
        Long entityId = 999L;
        when(entitiesRepository.getEntity(entityId)).thenReturn(null);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.getEntity(entityId));
        assertEquals(ApiMgmtErrorCode.ENTITY_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void update_withoutOperatorRole_shouldNotUpdateSettings() {
        Long entityId = 1L;
        UpdateEntityRequestDTO updateRequest = createUpdateRequest();

        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(any(EntityTypes.class))).thenReturn(false);
        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(EntityTypes.OPERATOR)).thenReturn(false);

        entitiesService.update(entityId, updateRequest);

        verify(entitiesRepository).update(argThat(entity -> 
            entity.getAllowPngConversion() == null
        ));
    }

    @Test
    void update_withCompleteSettings_shouldMapAllFields() {
        Long entityId = 1L;
        UpdateEntityRequestDTO updateRequest = createCompleteUpdateRequest();

        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(any(EntityTypes.class))).thenReturn(true);
        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(EntityTypes.OPERATOR)).thenReturn(true);

        entitiesService.update(entityId, updateRequest);

        verify(entitiesRepository).update(argThat(entity -> 
            entity.getAllowPngConversion() != null &&
            entity.getAllowPngConversion().equals(true) &&
            entity.getAllowFriends() != null &&
            entity.getAllowFriends().equals(true) &&
            entity.getAllowLoyaltyPoints() != null &&
            entity.getAllowLoyaltyPoints().equals(true)
        ));
    }

    private Entity createTestEntity() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("Test Entity");
        entity.setShortName("TEST");
        entity.setReference("REF001");
        entity.setNif("12345678A");
        entity.setState(EntityStatus.ACTIVE);
        entity.setTypes(Arrays.asList(EntityTypes.CHANNEL_ENTITY));
        
        entity.setAllowPngConversion(false);
        entity.setAllowFriends(false);
        entity.setAllowLoyaltyPoints(false);
        entity.setAllowMembers(true);
        entity.setUseMultieventCart(false);
        entity.setModuleB2BEnabled(false);
        
        entity.setLanguage(new IdValueCodeDTO(1L));
        
        return entity;
    }

    private Map<Long, String> createTestLanguages() {
        Map<Long, String> languages = new HashMap<>();
        languages.put(1L, "es-ES");
        languages.put(2L, "en-US");
        return languages;
    }

    private UpdateEntityRequestDTO createUpdateRequest() {
        UpdateEntityRequestDTO request = new UpdateEntityRequestDTO();
        request.setName("Updated Entity");
        
        EntitySettingsDTO settings = new EntitySettingsDTO();
        settings.setAllowMembers(true);
        request.setSettings(settings);
        
        return request;
    }

    private UpdateEntityRequestDTO createCompleteUpdateRequest() {
        UpdateEntityRequestDTO request = createUpdateRequest();
        
        EntitySettingsDTO settings = request.getSettings();
        settings.setAllowFriends(true);
        settings.setAllowLoyaltyPoints(true);
        settings.setAllowPngConversion(true);
        settings.setEnableMultieventCart(true);
        settings.setEnableB2B(false);
        settings.setAllowActivityEvents(true);
        
        return request;
    }
}
