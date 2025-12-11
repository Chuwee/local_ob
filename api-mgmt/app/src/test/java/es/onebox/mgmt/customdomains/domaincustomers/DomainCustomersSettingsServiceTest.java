package es.onebox.mgmt.customdomains.domaincustomers;

import es.onebox.mgmt.customdomains.common.converter.DomainSettingsConverter;
import es.onebox.mgmt.customdomains.common.dto.CustomDomainSetting;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsMode;
import es.onebox.mgmt.customdomains.customersdomain.domaincustomers.DomainCustomersSettingsService;
import es.onebox.mgmt.customdomains.common.dto.CustomDomain;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.customdomains.common.dto.DomainMode;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
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

class DomainCustomersSettingsServiceTest {

    @Mock
    private EntitiesRepository entitiesRepository;

    @InjectMocks
    private DomainCustomersSettingsService domainCustomersSettingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGet() {
        // Given
        Long entityId = 1L;
        DomainSettings customersDomainSettings = new DomainSettings();
        customersDomainSettings.setEnabled(true);
        customersDomainSettings.setMode(DomainMode.DEFAULT);
        customersDomainSettings.setDomains(List.of(new CustomDomain("example.com", false)));

        when(entitiesRepository.getCustomersDomainSettings(entityId)).thenReturn(customersDomainSettings);

        // When
        DomainSettingsDTO result = domainCustomersSettingsService.get(entityId);

        // Then
        assertNotNull(result);
        assertTrue(result.getEnabled());
        assertEquals(DomainSettingsMode.DEFAULT, result.getMode());
        assertNotNull(result.getDomains());
        assertEquals(1, result.getDomains().size());
        assertEquals("example.com", result.getDomains().get(0).getDomain());
        assertFalse(result.getDomains().get(0).getDefaultDomain());
        verify(entitiesRepository, times(1)).getCustomersDomainSettings(entityId);
    }

    @Test
    void testGet_NullResult() {
        // Given
        Long entityId = 2L;
        when(entitiesRepository.getCustomersDomainSettings(entityId)).thenReturn(null);

        // When
        DomainSettingsDTO result = domainCustomersSettingsService.get(entityId);

        // Then
        assertNull(result);
        verify(entitiesRepository, times(1)).getCustomersDomainSettings(entityId);
    }

    @Test
    void testUpsert() {
        // Given
        Long entityId = 3L;
        DomainSettingsDTO dto = new DomainSettingsDTO();
        dto.setEnabled(true);
        dto.setMode(DomainSettingsMode.DEFAULT);
        dto.setDomains(List.of(new CustomDomainSetting("example-upsert.com", true)));

        DomainSettings expectedCustomersDomainSettings = DomainSettingsConverter.fromDTO(dto);

        // When
        domainCustomersSettingsService.upsert(entityId, dto);

        // Then
        ArgumentCaptor<DomainSettings> captor = ArgumentCaptor.forClass(DomainSettings.class);
        verify(entitiesRepository, times(1)).upsertCustomersDomainSettings(
                any(),
                captor.capture()
        );

        DomainSettings capturedCustomersDomainSettings = captor.getValue();

        assertNotNull(capturedCustomersDomainSettings);
        assertTrue(capturedCustomersDomainSettings.getEnabled());
        assertEquals(expectedCustomersDomainSettings.getMode(), capturedCustomersDomainSettings.getMode());
        assertNotNull(capturedCustomersDomainSettings.getDomains());
        assertEquals(1, capturedCustomersDomainSettings.getDomains().size());
        assertEquals("example-upsert.com", capturedCustomersDomainSettings.getDomains().get(0).getDomain());
        assertTrue(capturedCustomersDomainSettings.getDomains().get(0).getDefaultDomain());
        verify(entitiesRepository, times(1)).upsertCustomersDomainSettings(any(), any());
    }

    @Test
    void testDisable() {
        // Given
        Long entityId = 4L;

        // When
        domainCustomersSettingsService.disable(entityId);

        // Then
        verify(entitiesRepository, times(1)).disableCustomersDomainSettings(entityId);
    }

}
