package es.onebox.event.timezone.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeZoneDaoTest extends DaoImplTest {

    @InjectMocks
    private TimeZoneDao timeZoneDao;

    @Test
    public void findByPriceZone() {
        CpanelTimeZoneGroupRecord result = timeZoneDao.findByPriceZone(10);
        assertEquals(37, result.getZoneid(), "Expected result is retrieved");
    }

    @Override
    protected String getDatabaseFile() {
        return "dao/TimeZoneDaoTest.sql";
    }
}
