package es.onebox.event.seasontickets.converter;

import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.seasontickets.dto.SessionAssignableDTO;
import es.onebox.event.seasontickets.dto.SessionAssignableReason;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeasonTicketSessionsConverterTest {

    @Test
    public void isAssignableTest() {
        SessionResultDTO sessionResult = new SessionResultDTO();

        sessionResult.setSessionStatus(SessionStatus.PLANNED.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        SessionAssignableDTO result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.PLANNED.getId());
        sessionResult.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.PLANNED.getId());
        sessionResult.setEventStatus(EventStatus.READY.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.PLANNED.getId());
        sessionResult.setEventStatus(EventStatus.CANCELLED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.SCHEDULED.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.SCHEDULED.getId());
        sessionResult.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.SCHEDULED.getId());
        sessionResult.setEventStatus(EventStatus.READY.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.SCHEDULED.getId());
        sessionResult.setEventStatus(EventStatus.CANCELLED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.READY.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.READY.getId());
        sessionResult.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertTrue(result.getAssignable());
        assertNull(result.getReason());

        sessionResult.setSessionStatus(SessionStatus.READY.getId());
        sessionResult.setEventStatus(EventStatus.READY.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.READY.getId());
        sessionResult.setEventStatus(EventStatus.CANCELLED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.CANCELLED.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.CANCELLED.getId());
        sessionResult.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.CANCELLED.getId());
        sessionResult.setEventStatus(EventStatus.READY.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.CANCELLED.getId());
        sessionResult.setEventStatus(EventStatus.CANCELLED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.FINALIZED.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.FINALIZED.getId());
        sessionResult.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.FINALIZED.getId());
        sessionResult.setEventStatus(EventStatus.READY.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.FINALIZED.getId());
        sessionResult.setEventStatus(EventStatus.CANCELLED.getId());
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS, result.getReason());

        sessionResult.setSessionStatus(SessionStatus.PLANNED.getId());
        sessionResult.setEventStatus(EventStatus.PLANNED.getId());
        sessionResult.setEventSeasonType(SessionPackType.RESTRICTED);
        result = SessionAssignableConverter.convert(sessionResult);
        assertFalse(result.getAssignable());
        assertEquals(SessionAssignableReason.SESSION_ASSIGNABLE_RESTRICTED, result.getReason());
    }

    @Test
    public void setStatusTest_assigned() {
        List<Integer> relatedSeasonSessionIds = Arrays.asList(1, 2);
        SessionResultDTO sessionResult = new SessionResultDTO();
        sessionResult.setRelatedSeasonSessionIds(relatedSeasonSessionIds);
        Integer seasonTicketSessionId = 1;

        SessionAssignationStatusDTO result = SeasonTicketSessionsConverter.setStatus(sessionResult, seasonTicketSessionId);

        assertNotNull(result);
        assertEquals(SessionAssignationStatusDTO.ASSIGNED, result);
    }

    @Test
    public void setStatusTest_not_assigned() {
        List<Integer> relatedSeasonSessionIds = Arrays.asList(1, 2);
        SessionResultDTO sessionResult = new SessionResultDTO();
        sessionResult.setRelatedSeasonSessionIds(relatedSeasonSessionIds);
        Integer seasonTicketSessionId = 3;

        SessionAssignationStatusDTO result = SeasonTicketSessionsConverter.setStatus(sessionResult, seasonTicketSessionId);

        assertNotNull(result);
        assertEquals(SessionAssignationStatusDTO.NOT_ASSIGNED, result);
    }
}
