package es.onebox.event.archiver;

import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.domain.Session;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDaoTest extends DaoImplTest {

    @InjectMocks
    private SessionDao sessionDao;

    @Override
    protected String getDatabaseFile() {
        return "db/obCpanelTest.sql";
    }

    @Test
    public void getFinalizedEventSessionIdsNow() {
        ZonedDateTime archivationDate = ZonedDateTime.now().withYear(2018).minusMonths(25);
        List<Session> finalizedSessions = sessionDao.getFinalizedEventSessions(archivationDate, new ArrayList<>());

        Assertions.assertEquals(8, finalizedSessions.size());
    }

    @Test
    public void getFinalizedEventSessionIdsAlternativeDate() {
        ZonedDateTime archivationDate = ZonedDateTime.now().withYear(2031);
        List<Session> finalizedSessions = sessionDao.getFinalizedEventSessions(archivationDate, new ArrayList<>());

        Assertions.assertEquals(9, finalizedSessions.size());
    }

    @Test
    public void getFinalizedEventWithoutAllSessions() {
        ZonedDateTime archivationDate = ZonedDateTime.now().withYear(2010);
        List<Session> finalizedSessions = sessionDao.getFinalizedEventSessions(archivationDate, new ArrayList<>());

        Assertions.assertEquals(2, finalizedSessions.size());
    }

}
