package es.onebox.event.attributes;

import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class EventAttributeDaoTest extends DaoImplTest {

    @InjectMocks
    private EventAttributeDao eventAttributeDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/EventAttributeDao.sql";
    }

    @Test
    public void getEventAttributes() {
        final Integer eventId = 10;
        List<CpanelAtributosEventoRecord> result = eventAttributeDao.getEventAttributes(eventId);
        Assertions.assertEquals(4, result.size(), "All objects are retrieved");
    }
}
