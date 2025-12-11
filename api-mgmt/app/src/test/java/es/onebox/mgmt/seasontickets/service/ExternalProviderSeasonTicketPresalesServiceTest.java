package es.onebox.mgmt.seasontickets.service;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBase;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBaseList;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.events.dto.ExternalPresaleBaseDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ExternalProviderSeasonTicketPresalesServiceTest {

    private final Long entityId = 1L;
    private final Long seasonTicketId = 1L;
    private final boolean skipUsed = false;

    @Mock
    private DispatcherRepository dispatcherRepository;
    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ExternalProviderSeasonTicketPresalesService externalProviderSeasonTicketPresalesService;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExternalPrivatePresales_entityNotAccessible() {

        org.mockito.Mockito.doThrow(new RuntimeException("Entity not accessible"))
                .when(validationService).getAndCheckSeasonTicket(seasonTicketId);

        Exception exception = assertThrows(RuntimeException.class, () -> externalProviderSeasonTicketPresalesService.getAllExternalPrivatePresales(seasonTicketId, skipUsed));

        assertEquals("Entity not accessible", exception.getMessage());
    }

    @Test
    void testGetAllExternalPrivatePresales_dispatcherReturnsEmpty() {
        ExternalPresaleBaseList externalPrivatePresaleBase = new ExternalPresaleBaseList();
        SeasonTicket mockSeasonTicket = new SeasonTicket();
        mockSeasonTicket.setEntityId(entityId);

        when(validationService.getAndCheckSeasonTicket(seasonTicketId)).thenReturn(mockSeasonTicket);
        when(dispatcherRepository.getExternalSeasonTicketPresales(entityId, seasonTicketId, skipUsed)).thenReturn(externalPrivatePresaleBase);

        List<ExternalPresaleBaseDTO> result = externalProviderSeasonTicketPresalesService.getAllExternalPrivatePresales(seasonTicketId, skipUsed);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllExternalPrivatePresales_dispatcherReturnsData() {
        ExternalPresaleBase presale = new ExternalPresaleBase();
        presale.setId("100");
        presale.setName("Test Presale");
        ExternalPresaleBaseList externalPrivatePresaleBase = new ExternalPresaleBaseList();
        externalPrivatePresaleBase.add(presale);
        SeasonTicket mockSeasonTicket = new SeasonTicket();
        mockSeasonTicket.setEntityId(entityId);

        when(validationService.getAndCheckSeasonTicket(seasonTicketId)).thenReturn(mockSeasonTicket);
        when(dispatcherRepository.getExternalSeasonTicketPresales(entityId, seasonTicketId, skipUsed)).thenReturn(externalPrivatePresaleBase);
        List<ExternalPresaleBaseDTO> result = externalProviderSeasonTicketPresalesService.getAllExternalPrivatePresales(seasonTicketId, skipUsed);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("100", result.get(0).getId());
        assertEquals("Test Presale", result.get(0).getName());
    }

}