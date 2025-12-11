package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SeasonTicketHelperTest {

    @InjectMocks
    private SeasonTicketHelper seasonTicketHelper;
    @Mock
    private SeasonTicketEventDao seasonTicketEventDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAndCheckSeasonTicket() {
        Long seasonTicketId = new Random().nextLong(1, 100);
        EventRecord eventRecord = new EventRecord();
        Map<EventRecord, List<VenueRecord>> map = new LinkedHashMap<>();
        map.put(eventRecord, List.of(new VenueRecord()));
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(map.entrySet().iterator().next());
        assertEquals(eventRecord, seasonTicketHelper.getAndCheckSeasonTicket(seasonTicketId));
    }

    @Test
    void getAndCheckSeasonTicket_DeletedEvent() {
        Long seasonTicketId = new Random().nextLong(1, 100);
        EventRecord eventRecord = new EventRecord();
        eventRecord.setEstado(0);
        Map<EventRecord, List<VenueRecord>> map = new LinkedHashMap<>();
        map.put(eventRecord, List.of(new VenueRecord()));
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(map.entrySet().iterator().next());
        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketHelper.getAndCheckSeasonTicket(seasonTicketId));
        assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
    }

    @Test
    void getAndCheckSeasonTicket_EventNotFound() {
        Long seasonTicketId = new Random().nextLong(1, 100);
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(null);
        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketHelper.getAndCheckSeasonTicket(seasonTicketId));
        assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
    }
}