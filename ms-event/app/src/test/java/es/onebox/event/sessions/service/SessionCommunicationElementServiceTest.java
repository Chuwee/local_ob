package es.onebox.event.sessions.service;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SessionCommunicationElementServiceTest {

    @Mock
    private EventDao eventDao;
    @Mock
    private StaticDataContainer staticDataContainer;
    @Mock
    private EventCommunicationElementDao communicationElementDao;
    @Mock
    private SessionValidationHelper sessionValidationHelper;
    @InjectMocks
    private SessionCommunicationElementsService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findCommunicationElementsBySessionId_ok() {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setEstado(EventStatus.READY.getId());
        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setPosition(1);
        record.setValor("value");
        record.setDestino(1);
        record.setIdioma(2);
        record.setIdelemento(1);
        record.setIdtag(1);
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(any(), any())).thenReturn(initSession(1));
        when(communicationElementDao.findCommunicationElements(any(), any(), any(), any()))
                .thenReturn(records);
        when(staticDataContainer.getLanguage(any())).thenReturn("language");
        when(staticDataContainer.getTagId(any())).thenReturn(1);
        Map.Entry<EventRecord, List<VenueRecord>> event = createEventArray().entrySet().iterator().next();
        List<EventCommunicationElementDTO> elems = service.findCommunicationElements(1L, 1L,
                new EventCommunicationElementFilter());
        assertEquals(EventCommunicationElementConverter.fromRecords(records, event.getKey(), staticDataContainer), elems);
    }

    private Map<EventRecord, List<VenueRecord>> createEventArray() {
        Map<EventRecord, List<VenueRecord>> events = new HashMap<>();
        events.put(createEventMapper(), null);
        return events;
    }

    private EventRecord createEventMapper() {
        EventRecord event = new EventRecord();
        event.setIdevento(52);
        return event;
    }

    private SessionRecord initSession(Integer id) {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(id);
        sessionRecord.setIdevento(1);
        sessionRecord.setEstado(SessionStatus.READY.getId());
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now()));
        sessionRecord.setFechapublicacion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechaventa(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechafinsesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusHours(2)));
        return sessionRecord;
    }

}
