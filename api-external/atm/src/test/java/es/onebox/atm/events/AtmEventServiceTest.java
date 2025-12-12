package es.onebox.atm.events;

import es.onebox.atm.events.service.AtmEventService;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.event.dto.PreSaleConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionConfigDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AtmEventServiceTest {

    @InjectMocks
    private AtmEventService atmEventService;

    @Mock
    private MsEventRepository msEventRepository;

    @Mock
    private CatalogRepository catalogRepository;

    private static MockedStatic<AuthenticationUtils> authenticationUtils;
    private static MockedStatic<AuthenticationService> authenticationService;
    private static MockedStatic<AuthContextUtils> authContextUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
        authenticationService = Mockito.mockStatic(AuthenticationService.class);
        authContextUtils = Mockito.mockStatic(AuthContextUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        authenticationUtils.close();
        authenticationService.close();
        authContextUtils.close();
    }

    @Test
    void getSessionPresaleInformation() {
        Long eventId = 1L;
        Long sessionId = 1L;
        String token = "Valid token";

        ZonedDateTime startDateExpected = ZonedDateTime.now();
        ZonedDateTime endDateExpected = ZonedDateTime.now().plusDays(1);

        //Session with presale
        SessionConfigDTO sessionConfigDTO = new SessionConfigDTO();
        PreSaleConfigDTO preSaleConfigDTO = new PreSaleConfigDTO();
        preSaleConfigDTO.setActive(false);
        preSaleConfigDTO.setStartDate(startDateExpected);
        preSaleConfigDTO.setEndDate(endDateExpected);
        sessionConfigDTO.setSessionId(sessionId.intValue());
        sessionConfigDTO.setPreSaleConfig(preSaleConfigDTO);

        when(AuthContextUtils.getToken()).thenReturn(token);
        when(catalogRepository.getSession(token, eventId, sessionId)).thenReturn(new ChannelSession());
        when(msEventRepository.getSessionConfig(sessionId.intValue())).thenReturn(sessionConfigDTO);

        PreSaleConfigDTO presaleResponse =
                atmEventService.getSessionPresaleInformation(eventId, sessionId);

        Assertions.assertFalse(presaleResponse.isActive());
        Assertions.assertEquals(startDateExpected, presaleResponse.getStartDate());
        Assertions.assertEquals(endDateExpected, presaleResponse.getEndDate());
        verify(catalogRepository, times(1)).getSession(token, eventId, sessionId);
        verify(msEventRepository, times(1)).getSessionConfig(sessionId.intValue());
    }

    @Test
    void getSessionPresaleInformationSessionNotFound() {
        Long eventId = 1L;
        Long sessionId = 1L;
        String token = "Valid token";
        String expectedErrorMessage = "Session not found";

        when(AuthContextUtils.getToken()).thenReturn(token);
        when(catalogRepository.getSession(token, eventId, sessionId)).thenReturn(new ChannelSession());
        when(msEventRepository.getSessionConfig(sessionId.intValue())).thenReturn(null);

        Exception exception = assertThrows(OneboxRestException.class, () ->
                atmEventService.getSessionPresaleInformation(eventId, sessionId));

        verify(catalogRepository, times(1)).getSession(token, eventId, sessionId);
        verify(msEventRepository, times(1)).getSessionConfig(sessionId.intValue());
        assertEquals(exception.getMessage(), expectedErrorMessage);
    }

    @Test
    void getSessionPresaleInformationPresaleNotFound() {
        Long eventId = 1L;
        Long sessionId = 1L;
        String token = "Valid token";
        String expectedErrorMessage = "Presale not found";

        //Session without presale
        SessionConfigDTO sessionConfigDTO = new SessionConfigDTO();

        when(AuthContextUtils.getToken()).thenReturn(token);
        when(catalogRepository.getSession(token, eventId, sessionId)).thenReturn(new ChannelSession());
        when(msEventRepository.getSessionConfig(sessionId.intValue())).thenReturn(sessionConfigDTO);

        Exception exception = assertThrows(OneboxRestException.class, () ->
                atmEventService.getSessionPresaleInformation(eventId, sessionId));

        verify(catalogRepository, times(1)).getSession(token, eventId, sessionId);
        verify(msEventRepository, times(1)).getSessionConfig(sessionId.intValue());
        assertEquals(exception.getMessage(), expectedErrorMessage);
    }

    @Test
    void getSessionPresaleInformationChannelWithoutVisibilityNotFound() {
        long eventId = 1L;
        Long sessionId = 1L;
        String token = "Valid token";
        String expectedErrorMessage = "Session not found";

        when(AuthContextUtils.getToken()).thenReturn(token);
        when(catalogRepository.getSession(token, eventId, sessionId)).thenReturn(null);

        Exception exception = assertThrows(OneboxRestException.class, () ->
                atmEventService.getSessionPresaleInformation(eventId, sessionId));

        verify(catalogRepository, times(1)).getSession(token, eventId, sessionId);
        verify(msEventRepository, times(0)).getSessionConfig(sessionId.intValue());
        assertEquals(exception.getMessage(), expectedErrorMessage);
    }
}
