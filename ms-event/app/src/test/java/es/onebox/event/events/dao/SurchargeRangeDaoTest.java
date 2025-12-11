package es.onebox.event.events.dao;

import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

/**
 * @author ignasi
 */
public class SurchargeRangeDaoTest extends DaoImplTest {

    @InjectMocks
    private SurchargeRangeDao surchargeRangeDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/SurchargeRangeDao.sql";
    }

    @Test
    public void getSurchargeRangeByEventTest() {
        List<CpanelRangoRecord> event1 = surchargeRangeDao.getEventSurchargeRangesByEventId(1);
        Assertions.assertEquals(1, event1.size());

        List<CpanelRangoRecord> event2 = surchargeRangeDao.getEventSurchargeRangesByEventId(2);
        Assertions.assertEquals(Double.valueOf(0.0), event2.get(0).getRangominimo());

        surchargeRangeDao.getEventPromotionSurchargeRangesByEventId(1);
        surchargeRangeDao.getEventInvitationSurchargeRangesByEventId(1);
    }

    @Test
    public void getSurchargeRangeByChannelTest() {
        List<CpanelRangoRecord> event1 = surchargeRangeDao.getChannelSurchargeRangesByChannelId(1);
        Assertions.assertEquals(1, event1.size());

        List<CpanelRangoRecord> event2 = surchargeRangeDao.getChannelSurchargeRangesByChannelId(2);
        Assertions.assertEquals(Double.valueOf(0.0), event2.get(0).getRangominimo());

        List<CpanelRangoRecord> promos = surchargeRangeDao.getChannelPromotionSurchargeRangesByChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), promos.get(0).getRangominimo());

        List<CpanelRangoRecord> inv = surchargeRangeDao.getChannelInvitationSurchargeRangesByChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), inv.get(0).getRangominimo());
    }

    @Test
    public void getSurchargeRangeByEventChannelTest() {
        List<CpanelRangoRecord> eventChannel = surchargeRangeDao.getEventChannelSurchargeRangesByEventChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), eventChannel.get(0).getRangominimo());

        List<CpanelRangoRecord> promos = surchargeRangeDao.getEventChannelPromotionSurchargeRangesByEventChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), promos.get(0).getRangominimo());

        List<CpanelRangoRecord> inv = surchargeRangeDao.getEventChannelInvitationSurchargeRangesByEventChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), inv.get(0).getRangominimo());
    }

    @Test
    public void getSurchargeRangeByChannelEventTest() {
        List<CpanelRangoRecord> channelEvent = surchargeRangeDao.getChannelEventSurchargeRangesByChannelEventId(1);
        Assertions.assertEquals(Double.valueOf(0.0), channelEvent.get(0).getRangominimo());

        List<CpanelRangoRecord> promos = surchargeRangeDao.getChannelEventPromotionSurchargeRangesByChannelEventId(1);
        Assertions.assertEquals(Double.valueOf(0.0), promos.get(0).getRangominimo());

        List<CpanelRangoRecord> inv = surchargeRangeDao.getChannelInvitationSurchargeRangesByChannelId(1);
        Assertions.assertEquals(Double.valueOf(0.0), inv.get(0).getRangominimo());
    }

    @Test
    public void getEmptySurchargeRangesTest() {
        List<CpanelRangoRecord> emptySurcharges = surchargeRangeDao.getEventSurchargeRangesByEventId(3);

        Assertions.assertEquals(true, emptySurcharges.isEmpty());
    }

}
