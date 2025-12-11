package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class SaleGroupDaoTest extends DaoImplTest {

    @InjectMocks
    private SaleGroupDao saleGroupDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/SaleGroupDao.sql";
    }

    @Test
    public void getByEventId_test() {
        List<CpanelCuposConfigRecord> records = saleGroupDao.getByEventId(1L);
        Assertions.assertEquals(3, records.size(), "There are three sale groups for event id: 1");
    }


}
