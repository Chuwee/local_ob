package es.onebox.event.seasontickets.dao;

import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VenueConfigDaoTest extends DaoImplTest {

    @InjectMocks
    private VenueConfigDao venueConfigDao;

    protected String getDatabaseFile() {
        return "dao/VenueConfigDao.sql";
    }

    @Test
    public void getVenueConfigStatus_ok() {
        VenueConfigStatusRecord deleted = venueConfigDao.getVenueConfigStatus(1);
        VenueConfigStatusRecord active = venueConfigDao.getVenueConfigStatus(2);
        VenueConfigStatusRecord processing = venueConfigDao.getVenueConfigStatus(3);
        VenueConfigStatusRecord error = venueConfigDao.getVenueConfigStatus(4);

        assertEquals(0, deleted.getEstado(), "Deleted venueConfig has a wrong value");
        assertEquals(1, active.getEstado(), "Active venueConfig has a wrong value");
        assertEquals(2, processing.getEstado(), "Processing venueConfig has a wrong value");
        assertEquals(3, error.getEstado(), "Error venueConfig has a wrong value");
    }

    @Test
    public void getVenueConfigStatus_notFound_returnsNull() {
        VenueConfigStatusRecord status = venueConfigDao.getVenueConfigStatus(999);
        assertNull(status, "When venue config is not in DB result should be null");
    }

}
