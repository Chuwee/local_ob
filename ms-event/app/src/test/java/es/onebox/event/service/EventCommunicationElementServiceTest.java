package es.onebox.event.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.service.EventCommunicationElementService;
import es.onebox.event.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class EventCommunicationElementServiceTest {

    @Mock
    private EventService eventService;
    @Mock
    private CommonCommunicationElementService commonCommunicationElementService;
    @InjectMocks
    private EventCommunicationElementService eventComElService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findCommunicationElementsByEventId_ok() {
        EventRecord event = new EventRecord();
        when(eventService.getAndCheckEvent(any())).thenReturn(event);

        EventCommunicationElementDTO eventCommunicationElementDTO = new EventCommunicationElementDTO();
        List<EventCommunicationElementDTO> eventCommunicationElementDTOList = Collections.singletonList(eventCommunicationElementDTO);
        when(commonCommunicationElementService.findCommunicationElements(any(), any(), any())).thenReturn(eventCommunicationElementDTOList);

        List<EventCommunicationElementDTO> elems = eventComElService.findCommunicationElements(1L, new EventCommunicationElementFilter());
        Mockito.verify(commonCommunicationElementService, times(1)).findCommunicationElements(any(), any(), any());
    }

    @Test
    public void updateCommunicationElementsByEventId_ok() {
        EventRecord event = new EventRecord();
        when(eventService.getAndCheckEvent(any())).thenReturn(event);

        //Create items
        List<EventCommunicationElementDTO> elements = ObjectRandomizer.randomListOf(EventCommunicationElementDTO.class, 3);
        elements.forEach(e -> e.setTagId(EventTagType.TEXT_TITLE_WEB.getId()));

        eventComElService.updateCommunicationElements(1L, elements);

        Mockito.verify(commonCommunicationElementService, times(1)).updateCommunicationElements(any(), any(), any());
    }
}
