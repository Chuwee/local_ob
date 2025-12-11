package es.onebox.event.seasontickets.service;

import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.ticket.dto.LinkSessionCapacityResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionCompatibilityValidationResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionUnlinkResponse;
import es.onebox.event.datasources.ms.ticket.enums.SessionCompatibilityValidationReason;
import es.onebox.event.datasources.ms.ticket.enums.SessionUnlinkReason;
import es.onebox.event.datasources.ms.ticket.repository.SeasonTicketRepository;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.service.EventService;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.loyaltypoints.seasontickets.service.SeasonTicketLoyaltyPointsService;
import es.onebox.event.seasontickets.dao.SessionElasticDao;
import es.onebox.event.seasontickets.dto.AssignSessionReason;
import es.onebox.event.seasontickets.dto.AssignSessionRequestDTO;
import es.onebox.event.seasontickets.dto.AssignSessionResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionValidationReason;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionValidationResponse;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionsDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.seasontickets.dto.UnAssignSessionReason;
import es.onebox.event.seasontickets.dto.UnAssignSessionResponseDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.service.SessionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

public class SeasonTicketSessionsServiceTests {

    @InjectMocks
    private SeasonTicketSessionsService service;

    @Mock
    private SessionElasticDao elasticDao;

    @Mock
    private SeasonTicketService seasonTicketService;

    @Mock
    private SessionService sessionService;

    @Mock
    private EventService eventService;

    @Mock
    private SeasonSessionDao seasonSessionDao;

    @Mock
    private RefreshDataService refreshDataService;

    @Mock
    private SeasonTicketRepository seasonTicketRepository;

    @Mock
    private SeasonTicketLoyaltyPointsService seasonTicketLoyaltyPointsService;

    @Mock
    private EventSecondaryMarketConfigService seasonTicketSecondaryMarketService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkSeasonTicketNoSeasonTicketFoundTest() {
        Long seasonTicketId = 1L;
        SeasonTicketSessionsSearchFilter filter = new SeasonTicketSessionsSearchFilter();
        Mockito.when(seasonTicketService.getSeasonTicket(any())).thenThrow(new OneboxRestException());

        Assertions.assertThrows(OneboxRestException.class, () ->
                service.searchCandidateSessions(filter, seasonTicketId));
    }

    @Test
    public void checkSeasonTicketTest() {
        Long seasonTicketId = 1L;
        Long venueId = 23L;
        Long entityId = 2L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        List<VenueDTO> venues = new ArrayList<>();
        VenueDTO venue = new VenueDTO();
        venue.setId(venueId);
        venues.add(venue);
        seasonTicketDTO.setVenues(venues);
        seasonTicketDTO.setSessionId(seasonTicketId.intValue());

        Mockito.when(seasonTicketService.getSeasonTicket(any())).thenReturn(seasonTicketDTO);

        SeasonTicketSessionsSearchFilter filter = new SeasonTicketSessionsSearchFilter();

        Mockito.when(elasticDao.getSeasonTicketCandidateSessionsDTO(any())).thenReturn(getFakeSessionResultDTOs());
        Mockito.when(elasticDao.getSessions(any(), any())).thenReturn(getFakeSearchResponse(entityId, venueId));
        Mockito.when(seasonTicketService.getGenerationStatus(any())).thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketSessionsDTO result = service.searchCandidateSessions(filter, seasonTicketId);
        Assertions.assertEquals("Event", result.getData().get(0).getEventName());
        Assertions.assertEquals("Session", result.getData().get(0).getSessionName());
        Assertions.assertEquals(123, result.getData().get(0).getEventId().intValue());
        Assertions.assertEquals(123, result.getData().get(0).getSessionId().intValue());
    }

    @Test
    public void searchSessionsTest_invalidGenerationStatus() {
        Long seasonTicketId = 1L;
        Long venueId = 23L;
        Long entityId = 2L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        List<VenueDTO> venues = new ArrayList<>();
        VenueDTO venue = new VenueDTO();
        venue.setId(venueId);
        venues.add(venue);
        seasonTicketDTO.setVenues(venues);
        seasonTicketDTO.setSessionId(seasonTicketId.intValue());

        Mockito.when(seasonTicketService.getSeasonTicket(any())).thenReturn(seasonTicketDTO);

        SeasonTicketSessionsSearchFilter filter = new SeasonTicketSessionsSearchFilter();

        Mockito.when(elasticDao.getSeasonTicketCandidateSessionsDTO(any())).thenReturn(getFakeSessionResultDTOs());
        Mockito.when(elasticDao.getSessions(any(), any())).thenReturn(getFakeSearchResponse(entityId, venueId));
        Mockito.when(seasonTicketService.getGenerationStatus(any())).thenReturn(SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketSessionsDTO result = service.searchCandidateSessions(filter, seasonTicketId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION.getMessage(), capturedException.getMessage());
    }

    @Test
    public void verifySessionTest_valid() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 3L;
        long sessionId = 2L;

        long entityId = 10L;
        long venueId = 11L;
        long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SessionCompatibilityValidationResponse response = new SessionCompatibilityValidationResponse();
        response.setResult(true);
        Mockito.when(seasonTicketRepository.validateSessionCompatibility(any(), any())).thenReturn(response);


        List<Long> sessionsBySessionPack = Arrays.asList(101L, 102L);
        Mockito.when(seasonSessionDao
                .findSessionsBySessionPackId(anyLong())).thenReturn(sessionsBySessionPack);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertTrue(result.getResult());
        assertNull(result.getReason());
    }

    @Test
    public void verifySessionTest_in_progress() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.SEASON_TICKET_GENERATION_STATUS_NOT_VALID, result.getReason());
    }

    @Test
    public void verifySessionTest_pending_publication() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.SEASON_TICKET_GENERATION_STATUS_NOT_VALID, result.getReason());
    }

    @Test
    public void verifySessionTest_different_entity() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(100L);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.DIFFERENT_ENTITY_IDS, result.getReason());
    }

    @Test
    public void verifySessionTest_different_venue() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(100L);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.DIFFERENT_VENUE_IDS, result.getReason());
    }

    @Test
    public void verifySessionTest_venuetemplate_not_graphic() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(11L);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.FALSE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.SESSION_VENUE_NOT_GRAPHIC, result.getReason());
    }

    @Test
    public void verifySessionTest_not_assignable() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.READY);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.READY);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.EVENT_OR_SESSION_STATUS_NOT_VALID, result.getReason());
    }

    @Test
    public void verifySessionTest_session_not_found() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        Mockito.when(sessionService.getSession(sessionId)).thenReturn(null);

        OneboxRestException exception = null;
        try {
            SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        } catch (OneboxRestException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Session not found", exception.getMessage());
    }

    @Test
    public void verifySessionTest_session_already_assigned() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 3L;
        long sessionId = 2L;

        long entityId = 10L;
        long venueId = 11L;
        long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SessionCompatibilityValidationResponse response = new SessionCompatibilityValidationResponse();
        response.setResult(true);
        Mockito.when(seasonTicketRepository.validateSessionCompatibility(any(), any())).thenReturn(response);


        List<Long> sessionsBySessionPack = Arrays.asList(101L, 102L, sessionId);
        Mockito.when(seasonSessionDao
                .findSessionsBySessionPackId(anyLong())).thenReturn(sessionsBySessionPack);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.SEASON_TICKET_SESSION_ALREADY_ASSIGNED, result.getReason());
    }

    @Test
    public void verifySessionTest_session_restricted_ko() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 3L;
        long sessionId = 2L;

        long entityId = 10L;
        long venueId = 11L;
        long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        eventDTO.setSessionPackType(SessionPackType.RESTRICTED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SessionCompatibilityValidationResponse response = new SessionCompatibilityValidationResponse();
        response.setResult(true);
        Mockito.when(seasonTicketRepository.validateSessionCompatibility(any(), any())).thenReturn(response);


        List<Long> sessionsBySessionPack = Arrays.asList(101L, 102L, sessionId);
        Mockito.when(seasonSessionDao
                .findSessionsBySessionPackId(anyLong())).thenReturn(sessionsBySessionPack);

        SeasonTicketSessionValidationResponse result = service.verifySession(seasonTicketId, sessionId, Boolean.FALSE);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertFalse(result.getResult());
        assertNotNull(result.getReason());
        assertEquals(SeasonTicketSessionValidationReason.SEASON_TICKET_SESSION_RESTRICTED, result.getReason());
    }

    @Test
    public void assignSessionTest_ok() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 3L;
        long sessionId = 2L;

        long entityId = 10L;
        long venueId = 11L;
        long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SessionCompatibilityValidationResponse response = new SessionCompatibilityValidationResponse();
        response.setResult(true);
        Mockito.when(seasonTicketRepository.validateSessionCompatibility(any(), any())).thenReturn(response);


        List<Long> sessionsBySessionPack = Arrays.asList(101L, 102L);
        Mockito.when(seasonSessionDao
                .findSessionsBySessionPackId(anyLong())).thenReturn(sessionsBySessionPack);

        AssignSessionRequestDTO assignSessionRequestDTO = new AssignSessionRequestDTO();
        assignSessionRequestDTO.setSessionId(sessionId);
        assignSessionRequestDTO.setUpdateBarcodes(false);

        LinkSessionCapacityResponse linkSessionCapacityResponse = new LinkSessionCapacityResponse();
        linkSessionCapacityResponse.setResult(Boolean.TRUE);
        Mockito.when(seasonTicketRepository.linkSessionCapacity(anyLong(), anyLong(), anyBoolean())).thenReturn(linkSessionCapacityResponse);

        AssignSessionResponseDTO assignSessionResponseDTO = service
                .assignSession(seasonTicketId, assignSessionRequestDTO);

        assertNotNull(assignSessionRequestDTO);
        assertNull(assignSessionResponseDTO.getReason());
        assertEquals(Boolean.TRUE, assignSessionResponseDTO.getResult());
    }

    @Test
    public void assignSessionTest_session_not_valid() {
        Long seasonTicketId = 1L;
        Long sessionId = 2L;

        Long entityId = 10L;
        Long venueId = 11L;
        Long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setEntityId(100L);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        AssignSessionRequestDTO assignSessionRequestDTO = new AssignSessionRequestDTO();
        assignSessionRequestDTO.setSessionId(sessionId);

        LinkSessionCapacityResponse linkSessionCapacityResponse = new LinkSessionCapacityResponse();
        linkSessionCapacityResponse.setResult(Boolean.TRUE);
        Mockito.when(seasonTicketRepository.linkSessionCapacity(anyLong(), anyLong(), anyBoolean())).thenReturn(linkSessionCapacityResponse);

        AssignSessionResponseDTO assignSessionResponseDTO = service
                .assignSession(seasonTicketId, assignSessionRequestDTO);

        assertNotNull(assignSessionResponseDTO);
        assertNotNull(assignSessionResponseDTO.getResult());
        assertFalse(assignSessionResponseDTO.getResult());
        assertNotNull(assignSessionResponseDTO.getReason());
        assertEquals(AssignSessionReason.DIFFERENT_ENTITY_IDS, assignSessionResponseDTO.getReason());
    }

    @Test
    public void assignSessionTest_seats_not_valid() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 3L;
        long sessionId = 2L;

        long entityId = 10L;
        long venueId = 11L;
        long eventId = 12L;

        SeasonTicketStatusResponseDTO seasonTicketStatus = new SeasonTicketStatusResponseDTO();
        seasonTicketStatus.setGenerationStatus(SeasonTicketInternalGenerationStatus.READY);
        seasonTicketStatus.setStatus(SeasonTicketStatusDTO.SET_UP);
        Mockito.when(seasonTicketService.getStatus(seasonTicketId)).thenReturn(seasonTicketStatus);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        seasonTicketDTO.setEntityId(entityId);
        VenueDTO venueDTO = new VenueDTO();
        venueDTO.setId(venueId);
        seasonTicketDTO.setVenues(new ArrayList<>());
        seasonTicketDTO.getVenues().add(venueDTO);
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEventId(eventId);
        sessionDTO.setEntityId(entityId);
        sessionDTO.setVenueId(venueId);
        sessionDTO.setStatus(SessionStatus.SCHEDULED);
        sessionDTO.setSessionType(SessionType.SESSION);
        sessionDTO.setVenueConfigGraphic(Boolean.TRUE);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(sessionDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEntityId(entityId);
        eventDTO.setStatus(EventStatus.PLANNED);
        Mockito.when(eventService.getEvent(eventId)).thenReturn(eventDTO);

        SessionCompatibilityValidationResponse response = new SessionCompatibilityValidationResponse();
        response.setResult(true);
        Mockito.when(seasonTicketRepository.validateSessionCompatibility(any(), any())).thenReturn(response);


        List<Long> sessionsBySessionPack = Arrays.asList(101L, 102L);
        Mockito.when(seasonSessionDao
                .findSessionsBySessionPackId(anyLong())).thenReturn(sessionsBySessionPack);

        AssignSessionRequestDTO assignSessionRequestDTO = new AssignSessionRequestDTO();
        assignSessionRequestDTO.setSessionId(sessionId);
        assignSessionRequestDTO.setUpdateBarcodes(false);

        LinkSessionCapacityResponse linkSessionCapacityResponse = new LinkSessionCapacityResponse();
        linkSessionCapacityResponse.setResult(Boolean.FALSE);
        linkSessionCapacityResponse.setReason(SessionCompatibilityValidationReason.AT_LEAST_ONE_SEAT_IS_NOT_FOUND_IN_TARGET_SESSION);
        Mockito.when(seasonTicketRepository.linkSessionCapacity(anyLong(), anyLong(), anyBoolean())).thenReturn(linkSessionCapacityResponse);

        AssignSessionResponseDTO assignSessionResponseDTO = service
                .assignSession(seasonTicketId, assignSessionRequestDTO);

        assertNotNull(assignSessionResponseDTO);
        assertNotNull(assignSessionResponseDTO.getResult());
        assertFalse(assignSessionResponseDTO.getResult());
        assertNotNull(assignSessionResponseDTO.getReason());
        assertEquals(AssignSessionReason.AT_LEAST_ONE_SEAT_IS_NOT_FOUND_IN_TARGET_SESSION, assignSessionResponseDTO.getReason());
        Mockito.verify(seasonSessionDao, Mockito.times(1)).disasociateSessionOfSeason(anyLong(), anyLong());
    }

    public List<SessionResultDTO> getFakeSessionResultDTOs() {

        List<SessionResultDTO> result = new ArrayList<>();
        SessionResultDTO sessionResultDTO = new SessionResultDTO();
        sessionResultDTO.setEventName("Event");
        sessionResultDTO.setSessionId(123);
        sessionResultDTO.setEventId(123);
        sessionResultDTO.setSessionName("Session");
        sessionResultDTO.setBeginSessionDate(new Timestamp(127389107301L));
        sessionResultDTO.setSessionStatus(SessionStatus.IN_PROGRESS.getId());
        sessionResultDTO.setEventStatus(EventStatus.IN_PROGRESS.getId());
        result.add(sessionResultDTO);
        return result;
    }

    public SearchResponse<SessionData> getFakeSearchResponse(Long entityId, Long venueId) {
/*
        BytesReference source = new BytesArray(
                "{\n" +
                        "          \"session\": {\n" +
                        "            \"sessionId\": 1753,\n" +
                        "            \"eventId\": 177,\n" +
                        "            \"sessionName\": \"Season_tickets_update_01\",\n" +
                        "            \"sessionStatus\": 2,\n" +
                        "            \"beginSessionDate\": 1582560681000,\n" +
                        "            \"publishSessionDate\": 1580554843000,\n" +
                        "            \"published\": false,\n" +
                        "            \"rates\": [\n" +
                        "              {\n" +
                        "                \"id\": 193,\n" +
                        "                \"name\": \"Rate\",\n" +
                        "                \"defaultRate\": true,\n" +
                        "                \"sessionId\": 1753\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"venueId\": " + venueId + ",\n" +
                        "            \"communicationElements\": [],\n" +
                        "            \"promotions\": [],\n" +
                        "            \"eventName\": \"Season_tickets_update_01\",\n" +
                        "            \"eventStatus\": 1,\n" +
                        "            \"entityId\": " + entityId + ",\n" +
                        "            \"producerId\": 24,\n" +
                        "            \"eventType\": 5,\n" +
                        "            \"seasonPackSession\": false\n" +
                        "          }\n" +
                        "      }");
 */
//        SearchHit hit = new SearchHit(1);
//        hit.sourceRef(source);
//        SearchHits hits = new SearchHits(new SearchHit[]{hit}, new TotalHits(5, TotalHits.Relation.EQUAL_TO), 10);
//        SearchResponseSections searchResponseSections = new SearchResponseSections(hits, null, null, false, null, null, 5);
//        return new SearchResponse(searchResponseSections, null, 8, 8, 0, 8, new ShardSearchFailure[]{}, null);

        SessionData responseData = new SessionData();
        Session response = new Session();
        response.setVenueId(venueId);
        response.setEntityId(entityId);
        response.setSessionId(1753L);
        response.setEventId(177L);
        response.setSessionName("Season_tickets_update_01");
        response.setSessionStatus((byte) 2);
        response.setBeginSessionDate(new Date(1582560681000L));
        response.setBeginSessionDate(new Date(1580554843000L));
        response.setPublished(false);
        responseData.setSession(response);

        Hit<SessionData> hit = Hit.of(h -> h
                .source(responseData)
                .index("eventdata"));
        HitsMetadata<SessionData> hitsMetadata = HitsMetadata.of(hm -> hm
                .hits(List.of(hit))
                .total(TotalHits.of(th -> th.value(5).relation(TotalHitsRelation.Eq)))
        );
        return SearchResponse.of(r -> r.hits(hitsMetadata).took(1).timedOut(false)
                .shards(new ShardStatistics.Builder().successful(1).failed(0).total(1).build()));
    }

    @Test
    public void unAssignSessionTest_valid() {
        Long seasonTicketId = 1L;
        Long seasonTicketSessionId = 10L;
        Long targetSessionId = 100L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setStatus(SeasonTicketStatusDTO.SET_UP);
        seasonTicketDTO.setSessionId(seasonTicketSessionId.intValue());
        Mockito.when(seasonTicketService.getSeasonTicket(eq(seasonTicketId))).thenReturn(seasonTicketDTO);

        // searchCandidateSessions
        Mockito.when(seasonTicketService.getGenerationStatus(eq(seasonTicketId))).thenReturn(SeasonTicketInternalGenerationStatus.READY);

        List<SessionResultDTO> seasonTicketCandidateSessions = new ArrayList<>();
        SessionResultDTO sessionResultDTO = new SessionResultDTO();
        sessionResultDTO.setRelatedSeasonSessionIds(Collections.singletonList(seasonTicketSessionId.intValue()));
        sessionResultDTO.setSessionId(targetSessionId.intValue());
        sessionResultDTO.setBeginSessionDate(new Timestamp(123L));
        sessionResultDTO.setEventStatus(EventStatus.PLANNED.getId());
        sessionResultDTO.setSessionStatus(SessionStatus.PLANNED.getId());
        seasonTicketCandidateSessions.add(sessionResultDTO);
        Mockito.when(elasticDao.getSeasonTicketCandidateSessionsDTO(any())).thenReturn(seasonTicketCandidateSessions);

        SessionUnlinkResponse unLinkResult = new SessionUnlinkResponse();
        unLinkResult.setResult(Boolean.TRUE);
        Mockito.when(seasonTicketRepository.unLinkSessionCapacity(eq(seasonTicketSessionId), eq(targetSessionId), anyBoolean())).thenReturn(unLinkResult);

        UnAssignSessionResponseDTO unAssignSessionResponseDTO = service.unAssignSession(seasonTicketId, targetSessionId, false);

        assertNotNull(unAssignSessionResponseDTO);
        assertNotNull(unAssignSessionResponseDTO.getResult());
        assertTrue(unAssignSessionResponseDTO.getResult());
        assertNull(unAssignSessionResponseDTO.getReason());
        Mockito.verify(seasonTicketRepository, Mockito.times(1)).unLinkSessionCapacity(anyLong(), anyLong(), anyBoolean());
        Mockito.verify(seasonSessionDao, Mockito.times(1)).disasociateSessionOfSeason(anyLong(), anyLong());
        Mockito.verify(seasonSessionDao, Mockito.never()).asociateSessionstoSeason(anyLong(), anyList());
    }

    @Test
    public void unAssignSessionTest_invalid_status() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 10L;
        long targetSessionId = 100L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        Mockito.when(seasonTicketService.getSeasonTicket(eq(seasonTicketId))).thenReturn(seasonTicketDTO);

        UnAssignSessionResponseDTO unAssignSessionResponseDTO = service.unAssignSession(seasonTicketId, targetSessionId, false);

        assertNotNull(unAssignSessionResponseDTO);
        assertNotNull(unAssignSessionResponseDTO.getResult());
        assertFalse(unAssignSessionResponseDTO.getResult());
        assertNotNull(unAssignSessionResponseDTO.getReason());
        assertEquals(UnAssignSessionReason.INVALID_SEASON_TICKET_STATUS, unAssignSessionResponseDTO.getReason());
        Mockito.verify(seasonTicketRepository, Mockito.never()).unLinkSessionCapacity(anyLong(), anyLong(), anyBoolean());
        Mockito.verify(seasonSessionDao, Mockito.never()).disasociateSessionOfSeason(anyLong(), anyLong());
        Mockito.verify(seasonSessionDao, Mockito.never()).asociateSessionstoSeason(anyLong(), anyList());
    }

    @Test
    public void unAssignSessionTest_session_not_valid() {
        long seasonTicketId = 1L;
        long seasonTicketSessionId = 10L;
        long targetSessionId = 100L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setStatus(SeasonTicketStatusDTO.SET_UP);
        seasonTicketDTO.setSessionId((int) seasonTicketSessionId);
        Mockito.when(seasonTicketService.getSeasonTicket(eq(seasonTicketId))).thenReturn(seasonTicketDTO);

        // searchCandidateSessions
        Mockito.when(seasonTicketService.getGenerationStatus(eq(seasonTicketId))).thenReturn(SeasonTicketInternalGenerationStatus.READY);

        List<SessionResultDTO> seasonTicketCandidateSessions = new ArrayList<>();
        SessionResultDTO sessionResultDTO = new SessionResultDTO();
        sessionResultDTO.setRelatedSeasonSessionIds(Collections.singletonList(123));
        sessionResultDTO.setSessionId((int) targetSessionId);
        sessionResultDTO.setBeginSessionDate(new Timestamp(123L));
        sessionResultDTO.setEventStatus(EventStatus.PLANNED.getId());
        sessionResultDTO.setSessionStatus(SessionStatus.PLANNED.getId());
        seasonTicketCandidateSessions.add(sessionResultDTO);
        Mockito.when(elasticDao.getSeasonTicketCandidateSessionsDTO(any())).thenReturn(seasonTicketCandidateSessions);

        UnAssignSessionResponseDTO unAssignSessionResponseDTO = service.unAssignSession(seasonTicketId, targetSessionId, false);

        assertNotNull(unAssignSessionResponseDTO);
        assertNotNull(unAssignSessionResponseDTO.getResult());
        assertFalse(unAssignSessionResponseDTO.getResult());
        assertNotNull(unAssignSessionResponseDTO.getReason());
        assertEquals(UnAssignSessionReason.SESSION_ID_NOT_VALID, unAssignSessionResponseDTO.getReason());
        Mockito.verify(seasonTicketRepository, Mockito.never()).unLinkSessionCapacity(anyLong(), anyLong(), anyBoolean());
        Mockito.verify(seasonSessionDao, Mockito.never()).disasociateSessionOfSeason(anyLong(), anyLong());
        Mockito.verify(seasonSessionDao, Mockito.never()).asociateSessionstoSeason(anyLong(), anyList());
    }

    @Test
    public void unAssignSessionTest_unLink_error() {
        Long seasonTicketId = 1L;
        Long seasonTicketSessionId = 10L;
        Long targetSessionId = 100L;

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setStatus(SeasonTicketStatusDTO.SET_UP);
        seasonTicketDTO.setSessionId(seasonTicketSessionId.intValue());
        Mockito.when(seasonTicketService.getSeasonTicket(eq(seasonTicketId))).thenReturn(seasonTicketDTO);

        // searchCandidateSessions
        Mockito.when(seasonTicketService.getGenerationStatus(eq(seasonTicketId))).thenReturn(SeasonTicketInternalGenerationStatus.READY);

        List<SessionResultDTO> seasonTicketCandidateSessions = new ArrayList<>();
        SessionResultDTO sessionResultDTO = new SessionResultDTO();
        sessionResultDTO.setRelatedSeasonSessionIds(Collections.singletonList(seasonTicketSessionId.intValue()));
        sessionResultDTO.setSessionId(targetSessionId.intValue());
        sessionResultDTO.setBeginSessionDate(new Timestamp(123L));
        sessionResultDTO.setEventStatus(EventStatus.PLANNED.getId());
        sessionResultDTO.setSessionStatus(SessionStatus.PLANNED.getId());
        seasonTicketCandidateSessions.add(sessionResultDTO);
        Mockito.when(elasticDao.getSeasonTicketCandidateSessionsDTO(any())).thenReturn(seasonTicketCandidateSessions);

        SessionUnlinkResponse unLinkResult = new SessionUnlinkResponse();
        unLinkResult.setResult(Boolean.FALSE);
        unLinkResult.setReason(SessionUnlinkReason.GENERIC_ERROR);
        Mockito.when(seasonTicketRepository.unLinkSessionCapacity(eq(seasonTicketSessionId), eq(targetSessionId), anyBoolean())).thenReturn(unLinkResult);

        UnAssignSessionResponseDTO unAssignSessionResponseDTO = service.unAssignSession(seasonTicketId, targetSessionId, false);

        assertNotNull(unAssignSessionResponseDTO);
        assertNotNull(unAssignSessionResponseDTO.getResult());
        assertFalse(unAssignSessionResponseDTO.getResult());
        assertNotNull(unAssignSessionResponseDTO.getReason());
        assertEquals(UnAssignSessionReason.GENERIC_ERROR, unAssignSessionResponseDTO.getReason());
        Mockito.verify(seasonTicketRepository, Mockito.times(1)).unLinkSessionCapacity(anyLong(), anyLong(), anyBoolean());
        Mockito.verify(seasonSessionDao, Mockito.times(1)).disasociateSessionOfSeason(anyLong(), anyLong());
        Mockito.verify(seasonSessionDao, Mockito.times(1)).asociateSessionstoSeason(anyLong(), anyList());
    }
}
