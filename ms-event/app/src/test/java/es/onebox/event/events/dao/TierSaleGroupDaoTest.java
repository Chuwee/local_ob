package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTierCupoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TierSaleGroupDaoTest extends DaoImplTest {

    @InjectMocks
    private TierSaleGroupDao tierSaleGroupDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/TierSaleGroupDao.sql";
    }

    @Test
    public void getByTierAndSaleGroup() {
        CpanelTierCupoRecord cpanelTierCupoRecord = tierSaleGroupDao.getByTierAndSaleGroup(2, 2);
        Assert.isNull(cpanelTierCupoRecord, "Tier 2 and sale group 2 don't have a relation");

        CpanelTierCupoRecord tierSaleGroup = tierSaleGroupDao.getByTierAndSaleGroup(1, 1);
        Assert.notNull(tierSaleGroup, "Tier 1 and sale group 1 have a relation");
        Assert.isTrue(tierSaleGroup.getLimite() == 10, "Limit is 10 for sale group 1 and tier 1");
    }

    @Test
    public void delete() {
        int deletedTiers = tierSaleGroupDao.delete(1, 1);
        assertEquals(1, deletedTiers, "A single record is deleted");
        deletedTiers = tierSaleGroupDao.delete(10, 1);
        assertEquals(0, deletedTiers, "No record is deleted");
    }

    @Test
    public void deleteByTierId() {
        int deletedTiers = tierSaleGroupDao.deleteByTierId(2);
        assertEquals(3, deletedTiers, "Three records are deleted");
        deletedTiers = tierSaleGroupDao.deleteByTierId(10);
        assertEquals(0, deletedTiers, "No record is deleted");
    }
}
