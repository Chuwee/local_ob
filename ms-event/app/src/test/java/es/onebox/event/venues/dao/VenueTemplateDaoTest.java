package es.onebox.event.venues.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class VenueTemplateDaoTest extends DaoImplTest {

    @InjectMocks
    private VenueTemplateDao venueTemplateDao;

    @Test
    public void findByPriceTypeId() {
        CpanelConfigRecintoRecord r = venueTemplateDao.findByPriceTypeId(1);
        Assertions.assertEquals(2474L, (long) r.getIdconfiguracion(), "Matching venue config is recovered");
    }

    @Override
    protected String getDatabaseFile() {
        return "dao/VenueTemplateDao.sql";
    }
}
