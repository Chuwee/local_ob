package es.onebox.service;

import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.RequestEntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.dto.UpdateExtReferenceRequest;
import es.onebox.fever.service.ExternalReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExternalReferenceServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long EVENT_ID = 2L;
    private static final Long SESSION_ID = 3L;
    private static final Long ENTITY_ID = 4L;

    private static final String EXTERNAL_REFERENCE = "123";

    @Mock
    private MsEventRepository msEventRepository;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private UsersRepository usersRepository;
    @InjectMocks
    private ExternalReferenceService externalReferenceService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateExternalReference_user_ok() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setUserId(USER_ID);

        when(usersRepository.getById(any())).thenReturn(new User());
        externalReferenceService.updateExternalReference(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository, times(1)).updateUser(eq(USER_ID), captor.capture());

        User userCaptured = captor.getValue();
        assertEquals(EXTERNAL_REFERENCE, userCaptured.getExternalReference());
    }

    @Test
    public void updateExternalReference_user_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setUserId(USER_ID);

        when(usersRepository.getById(any())).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.USER_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void updateExternalReference_event_ok() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setEventId(EVENT_ID);

        when(msEventRepository.getEvent(any())).thenReturn(new EventDTO());
        externalReferenceService.updateExternalReference(req);

        ArgumentCaptor<EventDTO> captor = ArgumentCaptor.forClass(EventDTO.class);
        verify(msEventRepository, times(1)).updateEvent(eq(EVENT_ID), captor.capture());

        EventDTO eventCaptured = captor.getValue();
        assertEquals(EXTERNAL_REFERENCE, eventCaptured.getExternalReference());
    }

    @Test
    public void updateExternalReference_event_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setEventId(EVENT_ID);

        when(msEventRepository.getEvent(any())).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.EVENT_NOT_FOUND.name(), ex.getErrorCode());
    }


    @Test
    public void updateExternalReference_session_ok() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setSessionId(SESSION_ID);
        req.setEventId(EVENT_ID);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(EVENT_ID);

        when(msEventRepository.getEvent(EVENT_ID)).thenReturn(eventDTO);
        when(msEventRepository.getSession(EVENT_ID, SESSION_ID)).thenReturn(new SessionDTO());
        externalReferenceService.updateExternalReference(req);

        ArgumentCaptor<UpdateSessionRequest> captor = ArgumentCaptor.forClass(UpdateSessionRequest.class);
        verify(msEventRepository, times(1)).updateSession(eq(EVENT_ID), eq(SESSION_ID), captor.capture());

        UpdateSessionRequest sessionCaptured = captor.getValue();
        assertEquals(EXTERNAL_REFERENCE, sessionCaptured.getExternalReference());
    }

    @Test
    public void updateExternalReference_session_noEvent_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setSessionId(SESSION_ID);
        req.setEventId(EVENT_ID);

        when(msEventRepository.getEvent(EVENT_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.EVENT_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void updateExternalReference_session_noSession_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setSessionId(SESSION_ID);
        req.setEventId(EVENT_ID);

        when(msEventRepository.getEvent(EVENT_ID)).thenReturn(new EventDTO());
        when(msEventRepository.getSession(EVENT_ID, SESSION_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.SESSION_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void updateExternalReference_entity_ok() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setEntityId(ENTITY_ID);

        EntityDTO entityDTO = new EntityDTO();

        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entityDTO);
        externalReferenceService.updateExternalReference(req);

        ArgumentCaptor<RequestEntityDTO> captor = ArgumentCaptor.forClass(RequestEntityDTO.class);
        verify(entitiesRepository, times(1)).updateEntity(eq(ENTITY_ID), captor.capture());

        RequestEntityDTO entityCaptured = captor.getValue();
        assertEquals(EXTERNAL_REFERENCE, entityCaptured.getExternalReference());
    }

    @Test
    public void updateExternalReference_entity_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");
        req.setEntityId(ENTITY_ID);

        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.ENTITY_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void updateExternalReference_cantResolveMode_ko() {
        UpdateExtReferenceRequest req = new UpdateExtReferenceRequest();
        req.setExternalReference("123");

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> externalReferenceService.updateExternalReference(req));

        assertEquals(ApiExternalErrorCode.BAD_REQUEST_PARAMETER.name(), ex.getErrorCode());
    }
}
