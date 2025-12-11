package es.onebox.mgmt.sessions;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBase;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBaseList;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.events.dto.ExternalPresaleBaseDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ExternalProviderPresalesServiceTest {

    private final Long entityId = 1L;
    private final Long sessionId = 1L;
    private final Long eventId = 1L;
    private final boolean skipUsed = false;

    @Mock
    private DispatcherRepository dispatcherRepository;
    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ExternalProviderPresalesService externalProviderPresalesService;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExternalPrivatePresales_entityNotAccessible() {

        org.mockito.Mockito.doThrow(new RuntimeException("Entity not accessible"))
                .when(validationService).getAndCheckSession(eventId, sessionId);

        Exception exception = assertThrows(RuntimeException.class, () -> externalProviderPresalesService.getAllExternalPrivatePresales(eventId, sessionId, skipUsed));

        assertEquals("Entity not accessible", exception.getMessage());
    }

    @Test
    void testGetAllExternalPrivatePresales_dispatcherReturnsEmpty() {
        ExternalPresaleBaseList externalPrivatePresaleBase = new ExternalPresaleBaseList();
        Session mockSession = new Session();
        mockSession.setEntityId(entityId);

        when(validationService.getAndCheckSession(eventId, sessionId)).thenReturn(mockSession);
        when(dispatcherRepository.getExternalPresales(entityId, eventId, sessionId, skipUsed)).thenReturn(externalPrivatePresaleBase);

        List<ExternalPresaleBaseDTO> result = externalProviderPresalesService.getAllExternalPrivatePresales(eventId, sessionId, skipUsed);

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
        Session mockSession = new Session();
        mockSession.setEntityId(entityId);

        when(validationService.getAndCheckSession(eventId, sessionId)).thenReturn(mockSession);
        when(dispatcherRepository.getExternalPresales(entityId, eventId, sessionId, skipUsed)).thenReturn(externalPrivatePresaleBase);
        List<ExternalPresaleBaseDTO> result = externalProviderPresalesService.getAllExternalPrivatePresales(eventId, sessionId, skipUsed);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("100", result.get(0).getId());
        assertEquals("Test Presale", result.get(0).getName());
    }
}