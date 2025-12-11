package es.onebox.event.seasontickets.dao;

import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Objects;

public class SeasonTicketSessionDaoTest extends DaoImplTest {

    @InjectMocks
    private SeasonTicketSessionDao seasonTicketSessionDao;

    protected String getDatabaseFile() {
        return "dao/SessionDao.sql";
    }

    @BeforeEach
    public void setUp() {
        super.setUp();

        SortOperator<String> sort = new SortOperator<>();
        sort.addDirection(Direction.ASC, "date");
    }

    @Test
    public void getSeasonTicketSession() {
        List<SessionRecord> records = seasonTicketSessionDao.searchSessionInfoByEventId(50L);
        SessionRecord record = records.get(0);
        Assertions.assertTrue(Objects.nonNull(record));

        Assertions.assertEquals(50, record.getIdevento().intValue());
        Assertions.assertEquals("2020-02-12 12:03:38.0", record.getFechainiciosesion().toString());
        Assertions.assertEquals("2021-02-12 12:03:38.0", record.getFechafinsesion().toString());
        Assertions.assertEquals("2020-02-12 12:03:38.0", record.getFechaventa().toString());
        Assertions.assertEquals("2020-02-12 12:03:38.0", record.getFechapublicacion().toString());
        Assertions.assertEquals(true, CommonUtils.isTrue(record.getPublicado()));
        Assertions.assertEquals(true, CommonUtils.isTrue(record.getEnventa()));
    }

    @Test
    public void getSeasonTicketWithTwoSessions() {
        List<SessionRecord> records = seasonTicketSessionDao.searchSessionInfoByEventId(52L);
        Assertions.assertTrue(records.size() == 2);
    }

    @Test
    public void getSeasonTicketSessionWithZeroSessions() {
        List<SessionRecord> records = seasonTicketSessionDao.searchSessionInfoByEventId(53L);
        Assertions.assertTrue(records.isEmpty());
    }
}
