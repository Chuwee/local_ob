package es.onebox.event.events.dao;

import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventDaoTest extends DaoImplTest {

    @InjectMocks
    private EventDao eventDao;

    protected String getDatabaseFile() {
        return "dao/EventDao.sql";
    }

    private EventSearchFilter eventFilter;
    private Map<EventRecord, List<VenueRecord>> eventRecords;

    @BeforeEach
    public void setUp() {
        super.setUp();
        eventFilter = new EventSearchFilter();

        SortOperator<String> sort = new SortOperator<>();
        sort.addDirection(Direction.ASC, "date");
        eventFilter.setSort(sort);

        eventRecords = null;
    }

    @Test
    public void find_WhereEntityInFilterIsNull_ReturnsEvents() {
        this.eventRecords = eventDao.findEvents(eventFilter);
        Assertions.assertTrue(eventRecords.keySet().size() > 0, "Events not found for null entity");
    }

    @Test
    public void find_WhereVenueConfigIdIsNotNull_ReturnsOneEvent() {
        eventFilter.setVenueConfigId(9L);
        eventRecords = eventDao.findEvents(eventFilter);
        Assertions.assertEquals(1, eventRecords.keySet().size(), "There are multiple events for venue: 9");
    }

    @Test
    public void find_WhereLimitIsSetToTwoElements_ReturnsTwoEvents() {
        eventFilter.setLimit(2L);
        eventRecords = eventDao.findEvents(eventFilter);
        Assertions.assertEquals(2, eventRecords.keySet().size(), "There are more than 2 events for limit: 2");
    }

    @Test
    public void find_WhereVenueEntityIdInFilterIs103_ReturnsTwoEvents() {
        eventFilter.setVenueEntityId(103L);
        eventRecords = eventDao.findEvents(eventFilter);
        Assertions.assertEquals(2, eventRecords.keySet().size(), "There are NOT 2 events of this venue entity: " + 103);
    }

    @Test
    public void countByFilter_WhereVenueEntityIdInFilterIs103_ReturnsTwoEvents() {
        eventFilter.setVenueEntityId(103L);
        Assertions.assertEquals(2L, eventDao.countByFilter(eventFilter));
    }

    @Test
    public void findByEventId_WhereEventIdIs285_ReturnEvent() {
        Map.Entry<EventRecord, List<VenueRecord>> event = eventDao.findEvent(285L);
        Assertions.assertEquals(285, event.getKey().getIdevento().intValue(), "Key incorrect");
    }

    @Test
    public void updateEventDates_WhereEventIdIs285_UpdatesRowWithDates() {
        Long eventId = 285L;
        Assertions.assertEquals(1, eventDao.updateEventDatesFromSessionCriteria(eventId));

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        Assertions.assertEquals(eventId.intValue(), event.getIdevento());
        Assertions.assertEquals(new Timestamp(1554102000000L), event.getFechainicio());
        Assertions.assertEquals(53, event.getFechainiciotz());
        Assertions.assertEquals(new Timestamp(1554305400000L), event.getFechafin());
        Assertions.assertEquals(10, event.getFechafintz());
        Assertions.assertEquals(new Timestamp(1554102000000L), event.getFechainicioreserva());
        Assertions.assertEquals(53, event.getFechainicioreservatz());
        Assertions.assertEquals(new Timestamp(1554102000000L), event.getFechafinreserva());
        Assertions.assertEquals(53, event.getFechafinreservatz());
        Assertions.assertEquals(new Timestamp(1554087600000L), event.getFechapublicacion());
        Assertions.assertEquals(73, event.getFechapublicaciontz());
        Assertions.assertEquals(new Timestamp(1554087600000L), event.getFechaventa());
        Assertions.assertEquals(73, event.getFechaventatz());
    }

    @Test
    public void getFinalizedEventIds_WhereTwoEventsAreFinalized_ReturnsTwoFinalizedEventIds() {
        List<Long> finalizedEventIds = eventDao.getFinalizedEventIds();
        Assertions.assertEquals(2, finalizedEventIds.size());
        Assertions.assertEquals(83L, finalizedEventIds.get(0));
        Assertions.assertEquals(129L, finalizedEventIds.get(1));
    }

    @Test
    public void archiveEvents_WhereOneIdIsPassed_ReturnOneRowUpdated() {
        int archivedEvents = eventDao.archiveEvents(Collections.singleton(285L));
        Assertions.assertEquals(1, archivedEvents);
    }

    @Test
    public void updateEventDates_WhereEventIdIs286AndSomeDatesAreNotSet_UpdatesRowWithDates() {
        Long eventId = 286L;

        Assertions.assertEquals(1, eventDao.updateEventDatesFromSessionCriteria(eventId));

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        Assertions.assertNull(event.getFechainicioreserva());
        Assertions.assertNull(event.getFechafinreserva());
    }

    @Test
    public void getEventJoinsExists() {
        Map.Entry<EventRecord, List<VenueRecord>> event = eventDao.findEvent(1L);
        EventRecord eventRecord = event.getKey();

        //Taxonomia base
        Assertions.assertEquals(Integer.valueOf(15), eventRecord.getIdtaxonomia());
        Assertions.assertEquals("ART", eventRecord.getCategoryCode());
        Assertions.assertEquals("Artes escénicas", eventRecord.getCategoryDescription());
        //Taxonomia propia
        Assertions.assertEquals(Integer.valueOf(1), eventRecord.getIdtaxonomiapropia());
        Assertions.assertEquals("001654", eventRecord.getCustomCategoryRef());
        Assertions.assertEquals("Custom category", eventRecord.getCustomCategoryDescription());
        //Promotor
        Assertions.assertEquals(Integer.valueOf(96), eventRecord.getIdpromotor());
        Assertions.assertEquals("Entidad TEST", eventRecord.getPromoterName());
        //Gira
        Assertions.assertEquals(Integer.valueOf(1), eventRecord.getIdgira());
        Assertions.assertEquals("Gira", eventRecord.getTourName());
    }

    @Test
    public void getEventJoinsNotExists() {
        Map.Entry<EventRecord, List<VenueRecord>> event = eventDao.findEvent(32L);
        EventRecord eventRecord = event.getKey();

        //Taxonomia base
        Assertions.assertNotNull(eventRecord.getIdtaxonomia());
        Assertions.assertNull(eventRecord.getCategoryCode());
        Assertions.assertNull(eventRecord.getCategoryDescription());
        //Taxonomia propia
        Assertions.assertNull(eventRecord.getIdtaxonomiapropia());
        Assertions.assertNull(eventRecord.getCustomCategoryRef());
        Assertions.assertNull(eventRecord.getCustomCategoryDescription());
        //Promotor
        Assertions.assertNotNull(eventRecord.getIdpromotor());
        Assertions.assertNull(eventRecord.getPromoterName());
        //Gira
        Assertions.assertNull(eventRecord.getIdgira());
        Assertions.assertNull(eventRecord.getTourName());
    }

    @Test
    public void countEventsByEntityAndName() {
        EventSearchFilter filter = new EventSearchFilter();
        filter.setEntityId(103L);
        filter.setName("Deleted event");
        Long amount = eventDao.countByFilter(filter);
        Long expected = 0L;
        Assertions.assertEquals(expected, amount, "When event is deleted, it's not counted");

        filter.setName("Existing name");
        amount = eventDao.countByFilter(filter);
        expected = 1L;
        Assertions.assertEquals(expected, amount, "When event is not deleted, it's counted");
    }
}
