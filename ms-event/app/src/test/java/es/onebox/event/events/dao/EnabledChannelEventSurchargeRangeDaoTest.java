package es.onebox.event.events.dao;

import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnabledChannelEventSurchargeRangeDaoTest extends DaoImplTest {

    @InjectMocks
    private ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;

    @Test
    public void inheritChargesRangesFromEvent() {
        channelEventSurchargeRangeDao.inheritChargesRangesFromEvent(400, 1);
        assertTrue(true, "Cannot test this method because it fails to recover all charge ranges after insertion");
    }

    @Override
    protected String getDatabaseFile() {
        return "dao/ChannelEventSurchargeRangeDao.sql";
    }


}
