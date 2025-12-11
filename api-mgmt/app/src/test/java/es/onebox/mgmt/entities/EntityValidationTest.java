package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.dto.EntityAccommodationsConfigDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityRequestDTO;
import es.onebox.mgmt.entities.enums.AccommodationsChannelEnablingMode;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class EntityValidationTest {

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @Mock
    private EntitiesRepository entitiesRepository;
    
    @Mock
    private SecurityManager securityManager;
    
    @Mock
    private MasterdataService masterdataService;

    @InjectMocks
    private EntitiesService entitiesService;

    private UpdateEntityRequestDTO validUpdateRequest;

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
        validUpdateRequest = createValidUpdateRequest();
    }

    @Test
    void validateUpdate_withValidSettings_shouldNotThrowException() {
        Long entityId = 1L;
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(createOperator());

        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(any(EntityTypes.class))).thenReturn(true);
        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(EntityTypes.OPERATOR)).thenReturn(true);
        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        assertDoesNotThrow(() -> entitiesService.update(entityId, validUpdateRequest));
    }

    @Test
    void validateUpdate_withInteractiveVenueEnabledButNoAllowedVenues_shouldThrowException() {
        Long entityId = 1L;
        SettingsInteractiveVenueDTO interactiveVenue = new SettingsInteractiveVenueDTO();
        interactiveVenue.setEnabled(true);
        interactiveVenue.setAllowedVenues(Collections.emptyList()); 
        validUpdateRequest.getSettings().setInteractiveVenue(interactiveVenue);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtErrorCode.ENTITY_INTERACTIVE_VENUE_ALLOWED_VENUES_MANDATORY.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withEmptyManagedEntities_shouldThrowException() {
        Long entityId = 1L;
        validUpdateRequest.getSettings().setManagedEntities(Collections.emptyList());

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtErrorCode.ENTITY_ADMIN_MANDATORY_MANAGED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withOperatorInManagedEntities_shouldThrowException() {
        Long entityId = 1L;
        Long operatorId = 999L;
        
        validUpdateRequest.getSettings().setManagedEntities(Arrays.asList(
            new IdNameDTO(operatorId, "Operator Entity")
        ));

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(operatorId);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getErrorCode(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Can't use an operator type"));
    }

    @Test
    void validateUpdate_withAccommodationsEnabledButNoVendors_shouldThrowException() {
        Long entityId = 1L;
        setAccommodationsConfigInRequest(true);

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withAccommodationsEnabledButNoChannelMode_shouldThrowException() {
        Long entityId = 1L;
        setAccommodationsConfigInRequest(true, null, null);

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withRestrictedModeButNoChannelIds_shouldThrowException() {
        Long entityId = 1L;
        setAccommodationsConfigInRequest(true, 
                                         AccommodationsChannelEnablingMode.RESTRICTED, 
                                         Collections.emptyList());

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withFeverZoneButOperatorDoesNotAllow_shouldThrowException() {
        Long entityId = 1L;
        Operator operator = createOperator();
        operator.setAllowFeverZone(false);
        
        validUpdateRequest.getSettings().setAllowFeverZone(true);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtErrorCode.FEVER_ZONE_NOT_ALLOWED_BY_OPERATOR.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withGatewayBenefitsButOperatorDoesNotAllow_shouldThrowException() {
        Long entityId = 1L;
        Operator operator = createOperator();
        operator.setAllowGatewayBenefits(false);
        
        validUpdateRequest.getSettings().setAllowGatewayBenefits(true);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);

        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        assertEquals(ApiMgmtErrorCode.GATEWAY_BENEFITS_NOT_ALLOWED_BY_OPERATOR.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withAllowPngConversionAndValidOperator_shouldNotThrowException() {
        Long entityId = 1L;
        Operator operator = createOperator();
        operator.setAllowFeverZone(true);
        operator.setAllowGatewayBenefits(true);
        
        validUpdateRequest.getSettings().setAllowPngConversion(true);
        validUpdateRequest.getSettings().setAllowFeverZone(true);
        validUpdateRequest.getSettings().setAllowGatewayBenefits(true);
        
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);

        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(any(EntityTypes.class))).thenReturn(true);
        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(EntityTypes.OPERATOR)).thenReturn(true);
        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        assertDoesNotThrow(() -> entitiesService.update(entityId, validUpdateRequest));
        
        verify(entitiesRepository).update(argThat(entity -> 
            entity.getAllowPngConversion() != null &&
            entity.getAllowPngConversion().equals(true)
        ));
    }

    @Test
    void validateUpdate_withMultipleValidationErrors_shouldThrowFirstError() {
        Long entityId = 1L;
        
        SettingsInteractiveVenueDTO interactiveVenue = new SettingsInteractiveVenueDTO();
        interactiveVenue.setEnabled(true);
        interactiveVenue.setAllowedVenues(Collections.emptyList()); // Error 1
        
        validUpdateRequest.getSettings().setInteractiveVenue(interactiveVenue);
        validUpdateRequest.getSettings().setManagedEntities(Collections.emptyList()); // Error 2

        OneboxRestException exception = assertThrows(OneboxRestException.class, 
            () -> entitiesService.update(entityId, validUpdateRequest));
        
        assertEquals(ApiMgmtErrorCode.ENTITY_INTERACTIVE_VENUE_ALLOWED_VENUES_MANDATORY.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateUpdate_withNullSettings_shouldNotThrowValidationException() {
        Long entityId = 1L;
        validUpdateRequest.setSettings(null);

        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(any(EntityTypes.class))).thenReturn(true);
        securityUtilsMockedStatic.when(() -> SecurityUtils.hasEntityType(EntityTypes.OPERATOR)).thenReturn(true);
        securityUtilsMockedStatic.when(SecurityUtils::getUserOperatorId).thenReturn(100L);

        assertDoesNotThrow(() -> entitiesService.update(entityId, validUpdateRequest));
    }

    private UpdateEntityRequestDTO createValidUpdateRequest() {
        UpdateEntityRequestDTO request = new UpdateEntityRequestDTO();
        request.setName("Valid Entity");
        
        EntitySettingsDTO settings = new EntitySettingsDTO();
        settings.setAllowMembers(true);
        settings.setManagedEntities(Arrays.asList(
            new IdNameDTO(1L, "Valid Managed Entity"),
            new IdNameDTO(2L, "Another Valid Entity")
        ));
        
        request.setSettings(settings);
        return request;
    }

    private Operator createOperator() {
        Operator operator = new Operator();
        operator.setId(100L);
        operator.setName("Test Operator");
        operator.setAllowFeverZone(true);
        operator.setAllowGatewayBenefits(true);
        return operator;
    }

    private void setAccommodationsConfigInRequest(boolean enabled) {
        setAccommodationsConfigInRequest(enabled, null, null);
    }

    private void setAccommodationsConfigInRequest(boolean enabled,
                                                  AccommodationsChannelEnablingMode channelMode,
                                                  List<Long> enabledChannelIds) {
        EntityAccommodationsConfigDTO accommodations = new EntityAccommodationsConfigDTO();
        accommodations.setEnabled(enabled);
        accommodations.setAllowedVendors(Arrays.asList());
        accommodations.setChannelEnablingMode(channelMode);
        accommodations.setEnabledChannelIds(enabledChannelIds);
        validUpdateRequest.getSettings().setAccommodationsConfig(accommodations);
    }
}