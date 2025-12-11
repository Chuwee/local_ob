package es.onebox.event.events;

import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class EventLanguageDaoTest extends DaoImplTest {

    @InjectMocks
    private EventLanguageDao eventLanguageDao;

    protected String getDatabaseFile() {
        return "dao/EventDao.sql";
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void find3ById() {
        List<EventLanguageRecord> eventLanguageRecords = eventLanguageDao.findByEventId(1L);
        Assertions.assertEquals(3, eventLanguageRecords.size());
        Assertions.assertEquals(1L, eventLanguageRecords.get(0).getId());
        Assertions.assertEquals("es_ES", eventLanguageRecords.get(0).getCode());
        Assertions.assertFalse(eventLanguageRecords.get(0).getDefault());
        Assertions.assertEquals(2L, eventLanguageRecords.get(1).getId());
        Assertions.assertEquals("ca_ES", eventLanguageRecords.get(1).getCode());
        Assertions.assertTrue(eventLanguageRecords.get(1).getDefault());
        Assertions.assertEquals(3L, eventLanguageRecords.get(2).getId());
        Assertions.assertEquals("en_US", eventLanguageRecords.get(2).getCode());
        Assertions.assertFalse(eventLanguageRecords.get(2).getDefault());
    }

    @Test
    public void find0ById() {
        List<EventLanguageRecord> eventLanguageRecords = eventLanguageDao.findByEventId(2L);
        Assertions.assertEquals(0, eventLanguageRecords.size());
    }
}
