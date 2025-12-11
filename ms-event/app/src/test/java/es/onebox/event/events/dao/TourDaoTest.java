package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.event.events.request.TourEventsFilter;
import es.onebox.event.events.request.ToursFilter;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Map;

public class TourDaoTest extends DaoImplTest {

    @InjectMocks
    private TourDao tourDao;

    protected String getDatabaseFile() {
        return "dao/TourDao.sql";
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getTourWithEvents() {
        TourEventsFilter filter = new TourEventsFilter();
        Map.Entry<TourRecord, List<EventRecord>> tour = tourDao.findWithEvents(1, filter);

        Assertions.assertEquals("Gira1", tour.getKey().getNombre());
        Assertions.assertEquals(2, tour.getValue().size());
    }

    @Test
    public void getTourNotFound() {
        TourEventsFilter filter = new TourEventsFilter();
        Map.Entry<TourRecord, List<EventRecord>> tour = tourDao.findWithEvents(5, filter);

        Assertions.assertNull(tour);
    }

    @Test
    public void findTours() {
        ToursFilter filter = new ToursFilter();
        filter.setOperatorId(1L);
        filter.setEntityId(1L);
        List<TourRecord> tours = tourDao.find(filter);
        Assertions.assertEquals(0, tours.size());

        filter.setEntityId(2L);
        tours = tourDao.find(filter);
        Assertions.assertEquals(3, tours.size());

        filter.setStatus(TourStatus.INACTIVE);
        tours = tourDao.find(filter);
        Assertions.assertEquals(2, tours.size());
    }

    @Test
    public void findToursEntityAdmin() {
        ToursFilter filter = new ToursFilter();
        filter.setOperatorId(1L);
        filter.setEntityAdminId(6707L);

        List<TourRecord> tours = tourDao.find(filter);
        Assertions.assertEquals(0, tours.size());

    }

    @Test
    public void countByName() {
        Long count = tourDao.countByNameAndEntity("Gira3", 2);
        Assertions.assertEquals(1L, count);

        count = tourDao.countByNameAndEntity("Gira3", 1);
        Assertions.assertEquals(0L, count);

        count = tourDao.countByNameAndEntity("newName", 2);
        Assertions.assertEquals(0L, count);
    }

}
