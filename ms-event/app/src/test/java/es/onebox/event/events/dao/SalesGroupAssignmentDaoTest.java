package es.onebox.event.events.dao;

import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.List;

public class SalesGroupAssignmentDaoTest extends DaoImplTest {

    @InjectMocks
    private SalesGroupAssignmentDao salesGroupAssignmentDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/SalesGroupAssignmentDao.sql";
    }

    @Test
    public void bulkInsertByEventChannelId() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        int channelEventId = 5;
        salesGroupAssignmentDao.bulkInsertByChannelEvent(ids, channelEventId);
        List<Long> saleGroups = salesGroupAssignmentDao.getChannelEventQuotaIds(channelEventId);
        Assertions.assertEquals(5, saleGroups.size(), "5 sale groups should be created");
        Assertions.assertTrue(saleGroups.containsAll(ids));
    }
}
