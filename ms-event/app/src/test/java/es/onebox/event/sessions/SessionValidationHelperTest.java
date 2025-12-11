package es.onebox.event.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


public class SessionValidationHelperTest {

    @Mock
    SessionDao sessionDao;
    @InjectMocks
    private SessionValidationHelper helper;

    private static final Integer EVENT_ID = 1;
    private static final Integer SESSION_ID = 2;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findSessionId_ok() {
        Mockito.when(sessionDao.findSession(any())).thenReturn(initSession(SESSION_ID));
        SessionRecord s = helper.getSessionAndValidateWithEvent(EVENT_ID.longValue(), SESSION_ID.longValue());
        assertEquals(SESSION_ID, s.getIdsesion());
        assertEquals(EVENT_ID, s.getIdevento());
    }

    @Test
    public void findSessionId__invalidEventId() {
        Assertions.assertThrows(OneboxRestException.class, () ->
                helper.getSessionAndValidateWithEvent(-1l, SESSION_ID.longValue()));
    }

    @Test
    public void findSessionId__invalidSessionId() {
        Assertions.assertThrows(OneboxRestException.class, () ->
                helper.getSessionAndValidateWithEvent(EVENT_ID.longValue(), -1l));
    }

    private SessionRecord initSession(Integer id) {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(id);
        sessionRecord.setIdevento(EVENT_ID);
        sessionRecord.setEstado(SessionStatus.READY.getId());
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now()));
        sessionRecord.setFechapublicacion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechaventa(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechafinsesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusHours(2)));
        return sessionRecord;
    }

}
