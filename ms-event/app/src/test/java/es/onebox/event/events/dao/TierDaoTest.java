package es.onebox.event.events.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TierDaoTest extends DaoImplTest {

    @InjectMocks
    private TierDao tierDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/TierDao.sql";
    }

    @Test
    public void countByZoneAndName() {
        long existingRecords = tierDao.countByZoneAndName(1, "Early Bird");
        assertEquals(1L, existingRecords, "Only one tier for zone 1 and name Early bird");

        existingRecords = tierDao.countByZoneAndName(1, "Early Grey");
        assertEquals(0L, existingRecords, "Zero tier for zone 1 and name Early Grey");
    }

    @Test
    public void findByEventId() {
        List<TierRecord> result = tierDao.findByEventId(123, null, null, null);
        assertTrue(result.isEmpty(), "No tier is found");

        result = tierDao.findByEventId(1234, null, null, null);
        assertEquals(5, result.size(), "All tiers are recovered");

        result = tierDao.findByEventId(1234, 10, null, null);
        assertEquals(3, result.size(), "Only tiers for venue template 10 are recovered");

        result = tierDao.findByEventId(1234, 20, null, null);
        assertEquals(2, result.size(), "Only tiers for venue template 10 are recovered");

        result = tierDao.findByEventId(1234, 30, null, null);
        assertTrue(result.isEmpty(), "No tier is found");

        result = tierDao.findByEventId(1234, null, 2, 3);
        assertEquals(2, result.size(), "Two tiers are recovered with pagination");

    }

    @Test
    public void findByZoneAndStartDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime theDate = ZonedDateTime.parse("2025-01-01 00:00:00 GMT", formatter);
        int zoneId = 1;
        CpanelTierRecord aTier = tierDao.findByZoneAndStartDate(zoneId, CommonUtils.zonedDateTimeToTimestamp(theDate));
        assertNotNull(aTier, "A tier is found");
    }

    @Test
    public void delete() {
        Integer tierToDelete = 1;
        tierDao.delete(tierToDelete);
        List<TierRecord> tiers = tierDao.findByEventId(1, null, null, null);
        assertTrue(tiers.stream().noneMatch(t -> tierToDelete.equals(t.getIdtier())), "Deleted tier not found");
    }

    @Test
    public void getTier() {
//        TierRecord record = tierDao.getTier(1);
//        Assertions.assertEquals((Integer) 1, record.getIdtier(), "matches tier id");
//        Assertions.assertEquals((Integer) 3, (Integer) record.getLimitesCupo().size(), "recovers all sales grous");
    }
}
