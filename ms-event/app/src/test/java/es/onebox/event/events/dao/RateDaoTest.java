package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.jooq.dao.test.DaoImplTest;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateDaoTest extends DaoImplTest {

    @InjectMocks
    private RateDao rateDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/RatesDao.sql";
    }

    @Test
    public void findEventRatesByEventIdTest() {

        List<RateRecord> result = rateDao.getRatesByEventId(22);
        assertTrue(result != null && !result.isEmpty(), "There are rates for event with id 22");
        assertEquals(3, result.size(), "There are 2 rates for event with id 22");
        assertEquals(3, result.get(0).getTranslations().size(), "event with id 22 has 3 translations in first rate");
        assertEquals(2, result.get(1).getTranslations().size(), "event with id 22 has 2 translations in rate rate");

        result = rateDao.getEventRatesByEventId(22, 1L, 1L);
        assertTrue(result != null && !result.isEmpty(), "There are rates for event with id 22 and offset");
        assertEquals(1, result.size(), "There are 1 rates for event with id 22 and offset");
        assertEquals(Integer.valueOf(2), result.get(0).getIdTarifa(), "event with id 22 and offset 1 is getting correct rate");

        result = rateDao.getRatesByEventId(666);
        assertTrue(result != null && result.isEmpty(), "There are rates for event with id 666");

        result = rateDao.getRatesByEventId(11);
        assertTrue(result != null && !result.isEmpty(), "There are rates for event with id 11");
        assertNull(result.get(0).getTranslations(), "There are not translation in rates of event 11");

    }

    @Test
    public void findEventRatesBySessionTest() {
        RatesFilter filter = new RatesFilter();

        Collection<RateRecord> result = rateDao.getRatesBySessionId(33, filter.getLimit(), filter.getOffset());
        assertTrue(result != null && !result.isEmpty(), "There are rates for session with id 33");

        result = rateDao.getRatesBySessionId(444, filter.getLimit(), filter.getOffset());
        assertTrue(result != null && result.isEmpty(), "There are rates for session with id 44");
    }


}
