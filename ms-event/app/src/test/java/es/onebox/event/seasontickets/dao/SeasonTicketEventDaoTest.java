package es.onebox.event.seasontickets.dao;

import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeasonTicketEventDaoTest extends DaoImplTest {

    @InjectMocks
    private SeasonTicketEventDao seasonTicketEventDao;

    protected String getDatabaseFile() {
        return "dao/EventDao.sql";
    }

    private SeasonTicketSearchFilter seasonTicketsFilter;
    private Map<EventRecord, List<VenueRecord>> seasonTickets;

    @BeforeEach
    public void setUp() {
        super.setUp();
        seasonTicketsFilter = new SeasonTicketSearchFilter();

        SortOperator<String> sort = new SortOperator<>();
        sort.addDirection(Direction.ASC, "date");
    }

    @Test
    public void countByFilter() {
        seasonTicketsFilter.setEntityId(103L);
        seasonTicketsFilter.setName("Deleted Season Ticket");
        Long amount = seasonTicketEventDao.countByFilter(seasonTicketsFilter);
        Long expected = 0L;
        assertEquals(expected, amount, "When the name is not found within the entity, the result is 0");

        seasonTicketsFilter.setName("Deleted Season Ticket");
        amount = seasonTicketEventDao.countByFilter(seasonTicketsFilter);
        expected = 0L;
        assertEquals(expected, amount, "When season ticket is deleted, it's not counted");

        seasonTicketsFilter.setName("Season Ticket");
        amount = seasonTicketEventDao.countByFilter(seasonTicketsFilter);
        expected = 1L;
        assertEquals(expected, amount, "When a season ticket is not deleted, it's counted");
    }

    @Test
    public void findSeasonTicket_WhereIdIs50_ReturnEvent() {
        Map.Entry<EventRecord, List<VenueRecord>> event = seasonTicketEventDao.findSeasonTicket(50L);
        assertEquals(event.getKey().getIdevento().intValue(), 50, "Key incorrect");
    }

    @Test
    public void getEventJoinsExists() {
        Map.Entry<EventRecord, List<VenueRecord>> seasonTicket = seasonTicketEventDao.findSeasonTicket(50L);
        EventRecord record = seasonTicket.getKey();

        //Taxonomia base
        assertEquals(Integer.valueOf(15), record.getIdtaxonomia());
        assertEquals("ART", record.getCategoryCode());
        assertEquals("Artes escénicas", record.getCategoryDescription());
        //Taxonomia propia
        assertEquals(Integer.valueOf(1), record.getIdtaxonomiapropia());
        assertEquals("001654", record.getCustomCategoryRef());
        assertEquals("Custom category", record.getCustomCategoryDescription());
        //Promotor
        assertEquals(Integer.valueOf(96), record.getIdpromotor());
        assertEquals("Entidad TEST", record.getPromoterName());
        //Gira
        assertEquals(Integer.valueOf(1), record.getIdgira());
        assertEquals("Gira", record.getTourName());
    }

    @Test
    public void getEventJoinsNotExists() {
        Map.Entry<EventRecord, List<VenueRecord>> event = seasonTicketEventDao.findSeasonTicket(51L);
        EventRecord record = event.getKey();

        //Taxonomia base
        Assertions.assertNotNull(record.getIdtaxonomia());
        Assertions.assertNull(record.getCategoryCode());
        Assertions.assertNull(record.getCategoryDescription());
        //Taxonomia propia
        Assertions.assertNull(record.getIdtaxonomiapropia());
        Assertions.assertNull(record.getCustomCategoryRef());
        Assertions.assertNull(record.getCustomCategoryDescription());
        //Promotor
        Assertions.assertNotNull(record.getIdpromotor());
        Assertions.assertNull(record.getPromoterName());
        //Gira
        Assertions.assertNull(record.getIdgira());
        Assertions.assertNull(record.getTourName());
    }

    @Test
    public void countEventsByEntityAndName() {
        SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();
        filter.setEntityId(103L);
        filter.setName("Deleted Season Ticket");
        Long amount = seasonTicketEventDao.countByFilter(filter);
        Long expected = 0L;
        assertEquals(expected, amount, "When event is deleted, it's not counted");

        filter.setName("Yet another season ticket");
        amount = seasonTicketEventDao.countByFilter(filter);
        expected = 1L;
        assertEquals(expected, amount, "When event is not deleted, it's counted");
    }

    @Test
    public void find_WhereEntityInFilterIsNull_ReturnsSeasonTickets() {
        this.seasonTickets = seasonTicketEventDao.findSeasonTickets(seasonTicketsFilter);
        Assertions.assertTrue(seasonTickets.keySet().size() > 0, "Events not found for null entity");
    }

    @Test
    public void find_WhereVenueConfigIdIsNotNull_ReturnsOneSeasonTicket() {
        seasonTicketsFilter = new SeasonTicketSearchFilter();
        seasonTicketsFilter.setVenueConfigId(10L);
        seasonTickets = seasonTicketEventDao.findSeasonTickets(seasonTicketsFilter);
        assertEquals(1, seasonTickets.keySet().size(), "There are multiple events for venue config id: 91");
    }

    @Test
    public void find_WhereLimitIsSetToTwoElements_ReturnsTwoSeasonTickets() {
        seasonTicketsFilter.setLimit(2L);
        seasonTickets = seasonTicketEventDao.findSeasonTickets(seasonTicketsFilter);
        assertEquals(2, seasonTickets.keySet().size(), "There are more than 2 events for limit: 2");
    }

    @Test
    public void find_WhereVenueEntityIdInFilterIs103_ReturnsTwoSeasonTickets() {
        seasonTicketsFilter.setVenueEntityId(103L);
        seasonTickets = seasonTicketEventDao.findSeasonTickets(seasonTicketsFilter);
        assertEquals(1, seasonTickets.keySet().size(), "There are NOT 2 events of this venue entity: " + 103);
    }
    @Test
    public void find_WhereEntityAdminIdInFilter() {
        seasonTicketsFilter.setEntityAdminId(6707L);
        seasonTickets = seasonTicketEventDao.findSeasonTickets(seasonTicketsFilter);
        assert(0 < seasonTickets.keySet().size());
    }
}
