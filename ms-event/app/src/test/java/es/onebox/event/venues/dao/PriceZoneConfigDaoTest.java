package es.onebox.event.venues.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class PriceZoneConfigDaoTest extends DaoImplTest {

    @InjectMocks
    private PriceTypeConfigDao priceZoneConfigDao;

    @Test
    public void getPriceZoneByEventId() {
        long eventId = 781;
        List<CpanelZonaPreciosConfigRecord> resutl = priceZoneConfigDao.getPriceZoneByEventId(eventId);
        Assertions.assertEquals(41, resutl.size(), "All expected results are recovered");
    }

    @Override
    protected String getDatabaseFile() {
        return "dao/PriceZoneConfigDao.sql";
    }
}
