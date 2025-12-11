package es.onebox.event.externalevents;

import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventRateDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventTypeDTO;
import es.onebox.event.externalevents.dao.ExternalEventRatesDao;
import es.onebox.event.externalevents.dao.ExternalEventsDao;
import es.onebox.event.externalevents.service.ExternalEventsService;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRatesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ExternalEventsServiceTest {

    @Mock
    private ExternalEventsDao externalEventsDao;

    @Mock
    private ExternalEventRatesDao externalEventRatesDao;

    @InjectMocks
    private ExternalEventsService service;

    @Captor
    private ArgumentCaptor<Set<CpanelExternalEventRecord>> newExternalEventsCaptor;

    @Captor
    private ArgumentCaptor<CpanelExternalEventRecord> existingExternalEventsCaptor;

    @Captor
    private ArgumentCaptor<Set<CpanelExternalEventRatesRecord>> newExternalEventRatesCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void upsertExternalEventsTest() {
        ExternalEventDTO newEvent = createExternalEventDTO("newEvent", 1, "newEvent", ExternalEventTypeDTO.EVENT);
        ExternalEventDTO existingEventAOnOtherEntity = createExternalEventDTO("existingEventA", 2, "existingEventA", ExternalEventTypeDTO.SEASON_TICKET);
        ExternalEventDTO existingEvent = createExternalEventDTO("existingEventB", 1, "existingEventB modif", ExternalEventTypeDTO.EVENT);

        List<CpanelExternalEventRecord> allExistingExternalEvents = existingEvents();
        Mockito.when(externalEventsDao.getExternalEvents(Mockito.anyList(), Mockito.eq(null))).thenReturn(allExistingExternalEvents);

        service.upsertExternalEvents(new ExternalEventDTO[]{newEvent, existingEventAOnOtherEntity, existingEvent});

        verify(externalEventsDao, times(1)).insertBatch(newExternalEventsCaptor.capture());

        Set<CpanelExternalEventRecord> newExternalEvents = newExternalEventsCaptor.getValue();
        assertNotNull(newExternalEvents);
        assertEquals(2, newExternalEvents.size());

        CpanelExternalEventRecord capturedNewEvent = newExternalEvents.stream()
                .filter(newExternalEvent -> newExternalEvent.getExternaleventid().equals("newEvent"))
                .findFirst()
                .orElse(null);
        assertNotNull(capturedNewEvent);
        assertEquals("newEvent", capturedNewEvent.getExternaleventid());
        assertEquals(1, capturedNewEvent.getEntityid().intValue());
        assertEquals("newEvent", capturedNewEvent.getName());
        assertEquals((byte) 1, capturedNewEvent.getEventtype().byteValue());

        CpanelExternalEventRecord capturedExistingEventAOnOtherEntity = newExternalEvents.stream()
                .filter(newExternalEvent -> newExternalEvent.getExternaleventid().equals("existingEventA"))
                .findFirst()
                .orElse(null);
        assertNotNull(capturedExistingEventAOnOtherEntity);
        assertEquals("existingEventA", capturedExistingEventAOnOtherEntity.getExternaleventid());
        assertEquals(2, capturedExistingEventAOnOtherEntity.getEntityid().intValue());
        assertEquals("existingEventA", capturedExistingEventAOnOtherEntity.getName());
        assertEquals((byte) 2, capturedExistingEventAOnOtherEntity.getEventtype().byteValue());

        verify(externalEventsDao, times(1)).update(existingExternalEventsCaptor.capture());

        CpanelExternalEventRecord capturedExistingExternalEvents = existingExternalEventsCaptor.getValue();
        assertNotNull(capturedExistingExternalEvents);
        assertEquals("existingEventB", capturedExistingExternalEvents.getExternaleventid());
        assertEquals(1, capturedExistingExternalEvents.getEntityid().intValue());
        assertEquals("existingEventB modif", capturedExistingExternalEvents.getName());
        assertEquals((byte) 1, capturedExistingExternalEvents.getEventtype().byteValue());
    }

    private List<CpanelExternalEventRecord> existingEvents() {
        CpanelExternalEventRecord existingEventA = new CpanelExternalEventRecord();
        existingEventA.setInternalid(1);
        existingEventA.setExternaleventid("existingEventA");
        existingEventA.setEntityid(1);
        existingEventA.setName("existingEventA");
        existingEventA.setEventtype((byte) 1);

        CpanelExternalEventRecord existingEventB = new CpanelExternalEventRecord();
        existingEventB.setInternalid(2);
        existingEventB.setExternaleventid("existingEventB");
        existingEventB.setEntityid(1);
        existingEventB.setName("existingEventB");
        existingEventB.setEventtype((byte) 2);

        return Arrays.asList(existingEventA, existingEventB);
    }

    private ExternalEventDTO createExternalEventDTO(String eventId, Integer entityId, String eventName,
                                                    ExternalEventTypeDTO eventType) {
        ExternalEventDTO externalEventDTO = new ExternalEventDTO();
        externalEventDTO.setEventId(eventId);
        externalEventDTO.setEntityId(entityId);
        externalEventDTO.setEventName(eventName);
        externalEventDTO.setEventType(eventType);
        return externalEventDTO;
    }

    @Test
    public void upsertExternalEventRatesTest() {
        ExternalEventRateDTO notExistingRateEventA = new ExternalEventRateDTO();
        notExistingRateEventA.setEventId("existingEventA");
        notExistingRateEventA.setEntityId(1);
        notExistingRateEventA.setRateName("notExistingRate");

        ExternalEventRateDTO existingRateEventA = new ExternalEventRateDTO();
        existingRateEventA.setEventId("existingEventA");
        existingRateEventA.setEntityId(1);
        existingRateEventA.setRateName("existingRate");

        ExternalEventRateDTO notExistingRateFromNotExistingEvent = new ExternalEventRateDTO();
        notExistingRateFromNotExistingEvent.setEventId("notExistingEvent");
        notExistingRateFromNotExistingEvent.setEntityId(1);
        notExistingRateFromNotExistingEvent.setRateName("notExistingRate");

        List<CpanelExternalEventRecord> allExistingExternalEvents = existingEvents();
        Mockito.when(externalEventsDao.getExternalEvents(Mockito.anyList(), Mockito.eq(null))).thenReturn(allExistingExternalEvents);

        CpanelExternalEventRatesRecord existingRate = new CpanelExternalEventRatesRecord(1, 1, "existingRate");
        Mockito.when(externalEventRatesDao.getRatesForExternalEvents(Mockito.anyCollection())).thenReturn(Collections.singletonList(existingRate));

        service.upsertExternalEventRates(new ExternalEventRateDTO[]{notExistingRateEventA, existingRateEventA, notExistingRateFromNotExistingEvent});

        verify(externalEventRatesDao, times(1)).insertBatch(newExternalEventRatesCaptor.capture());

        Set<CpanelExternalEventRatesRecord> newExternalEventRates = newExternalEventRatesCaptor.getValue();
        assertNotNull(newExternalEventRates);
        assertEquals(1, newExternalEventRates.size());

        CpanelExternalEventRatesRecord capturedNewRate = newExternalEventRates.stream()
                .findFirst()
                .orElse(null);
        assertNotNull(capturedNewRate);
        assertNull(capturedNewRate.getRateid());
        assertEquals(Integer.valueOf(1), capturedNewRate.getExternaleventinternalid());
        assertEquals("notExistingRate", capturedNewRate.getRatename());
    }
}
