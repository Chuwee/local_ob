package es.onebox.event.sessions.dao;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionDaoTest extends DaoImplTest {

    private static final ZonedDateTime from = ZonedDateTime.of(LocalDateTime.of(2018, Month.DECEMBER, 10, 14, 30), ZoneOffset.UTC);
    private static final ZonedDateTime to = ZonedDateTime.now();

    @InjectMocks
    private SessionDao sessionDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/SessionDao.sql";
    }

    @Test
    public void bulkInsertSessions() {
        Long amountOfSessions = 100L;
        List<Long> createdSessionIds = sessionDao.bulkInsertSessions(createSessions(amountOfSessions));
        List<Session> sessions = sessionDao.findSessionsById(createdSessionIds);
        createdSessionIds.forEach(id ->
                assertNotNull(sessions.stream().filter(s -> s.getSessionId().equals(id)).findAny().orElse(null),
                        "There is no session for id: " + id));
    }

    @Test
    public void whenArchivesSessionTheSessionIsArchived() {
        Long amountOfSessions = 10L;
        sessionDao.bulkInsertSessions(createSessions(amountOfSessions));
        List<Long> sessionIds = new ArrayList<>();
        Long idSesion = 1L;
        sessionIds.add(idSesion);
        sessionDao.archiveSessions(sessionIds);
        List<Session> sessions = sessionDao.findSessionsById(sessionIds);
        assertNotNull(sessions.stream().filter(session -> session.getSessionId().equals(idSesion)).findAny().orElse(null),
                "There is no session for id: " + idSesion);
    }

    @Test
    public void find_bySessionId() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setId(2L);
        List<SessionRecord> records = sessionDao.findSessions(sessionFilter, null);
        assertTrue(records.size() < 2, "More than 1 session found when finding by Id");
        assertEquals(2, records.get(0).getIdsesion(), "Session Id is not 2 when set to filter");
    }

    @Test
    public void find_byOperatorId_NoMatches() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setOperatorId(2L);
        List<SessionRecord> records = sessionDao.findSessions(sessionFilter, null);
        assertTrue(CollectionUtils.isEmpty(records));
    }

    @Test
    public void find_byOperatorId() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setOperatorId(1L);
        List<SessionRecord> records = sessionDao.findSessions(sessionFilter, null);
        assertTrue(CollectionUtils.isNotEmpty(records));
    }


    @Test
    public void find_WhereLimitIsSetToTwoElements_ReturnsTwoSessions() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setLimit(2L);
        List<SessionRecord> records = sessionDao.findSessions(sessionFilter, null);
        assertEquals(2, records.size(), "There are more than 2 sessions for limit: 2");
    }

    @Test
    public void find_WhereEventIdInFilterIsTwo() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        List<SessionRecord> records = sessionDao.findSessions(sessionFilter, null);
        assertEquals(5, records.size(), "Amount of sessions returned is NOT three for eventId: 2");
        assertTrue(records.stream().allMatch(r -> r.getIdevento().equals(2)), "Session eventId not 2 when specified by filter");
    }

    @Test
    public void getTest() {
        SessionRecord sessionRecord = sessionDao.findSession(2L);
        assertEquals(2, sessionRecord.getIdsesion(), "SessionId is not 2");
    }


    @Test
    public void countByFilter_WhenEventIdisTwo() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        assertEquals(5, sessionDao.countByFilter(sessionFilter));
    }

    @Test
    public void countByFilter_When_STATUS_is_PREVIEW() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        sessionFilter.setStatus(Collections.singletonList(SessionStatus.PREVIEW));
        List<SessionRecord> sessions = sessionDao.findSessions(sessionFilter, null);
        assertEquals(1, sessions.size());
        assertEquals(6, sessions.get(0).getIdsesion());
    }

    @Test
    public void countByFilter_When_STATUS_is_READY() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        sessionFilter.setStatus(Collections.singletonList(SessionStatus.READY));
        List<SessionRecord> sessions = sessionDao.findSessions(sessionFilter, null);
        assertEquals(1, sessions.size());
        assertEquals(5, sessions.get(0).getIdsesion());
    }

    @Test
    public void countByFilter_When_STATUS_is_READY_AND_PREVIEW() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        sessionFilter.setStatus(Arrays.asList(SessionStatus.READY, SessionStatus.PREVIEW));
        List<SessionRecord> sessions = sessionDao.findSessions(sessionFilter, null);
        assertEquals(2, sessions.size());
    }

    @Test
    public void countByFilter_When_STATUS_is_READY__PREVIEW_AND_SCHEDULED() {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setEventId(Collections.singletonList(2L));
        sessionFilter.setStatus(Arrays.asList(SessionStatus.READY, SessionStatus.PREVIEW, SessionStatus.PLANNED));
        List<SessionRecord> sessions = sessionDao.findSessions(sessionFilter, null);
        assertEquals(5, sessions.size());
    }

    private List<Session> createSessions(Long quantity) {
        List<Session> sessionsDAOList = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();
        for (Long i = 0L; i < quantity; i++) {
            sessionsDAOList.add(createSession(now, i));
        }
        return sessionsDAOList;
    }

    private Session createSession(ZonedDateTime now, Long i) {
        Session session = new Session();
        session.setEventId(i);
        session.setName("nombre: " + i);
        session.setStatus(i.intValue());
        session.setSeasonPass(false);
        session.setPublished(true);
        session.setOnSale(true);
        session.setBookings(true);
        session.setVenueEntityConfigId(i);
        session.setAccessValidationSpaceId(i);
        session.setSessionStartDate(now);
        session.setPublishDate(now.minusDays(10));
        session.setSalesDate(now.minusDays(10));
        session.setSessionEndDate(now.plusDays(1));
        session.setSessionRealEndDate(now.plusDays(1));
        session.setBookingStartDate(now.minusDays(9));
        session.setBookingEndDate(now.minusDays(1));
        session.setSaleType(i.intValue());
        session.setUseTemplateAccess(true);
        session.setUseLimitsQuotasTemplateEvent(true);
        session.setTypeScheduleAccess(i.intValue());
        session.setTaxId(i);
        session.setChargeTaxId(i);
        session.setFinalDate(true);
        return session;
    }
}
