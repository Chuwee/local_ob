package es.onebox.event.events.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.TourDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.dto.BaseTourDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.TourDTO;
import es.onebox.event.events.dto.ToursDTO;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.request.TourEventsFilter;
import es.onebox.event.events.request.ToursFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import es.onebox.message.broker.producer.MessageProducer;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TourServiceTest {

    @InjectMocks
    private TourService tourService;

    @Mock
    private TourDao tourDao;

    @Mock
    private EventCommunicationElementDao communicationElementDao;

    @Mock
    private StaticDataContainer staticDataContainer;

    @Mock
    private S3BinaryRepository s3BinaryRepository;

    @Mock
    private RefreshDataService refreshDataService;

    @Mock
    private MessageProducer eventMigrationProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getTourOK() {
        Map<TourRecord, List<EventRecord>> tourWithEvents = new HashMap<>();
        EventRecord event = new EventRecord();
        event.setIdevento(1);
        tourWithEvents.put(buildTourRecord(), Arrays.asList(event, event));
        TourEventsFilter filter = new TourEventsFilter();
        when(tourDao.findWithEvents(1, filter)).thenReturn(tourWithEvents.entrySet().iterator().next());

        TourDTO tour = tourService.getTour(1L, filter);
        Assertions.assertEquals(Long.valueOf(1L), tour.getId());
        Assertions.assertEquals(2, tour.getEvents().size());
    }

    @Test
    public void getTour_deleted() {
        Map<TourRecord, List<EventRecord>> tourWithEvents = new HashMap<>();
        TourRecord tour = buildTourRecord();
        tour.setEstado(0);
        tourWithEvents.put(tour, null);
        TourEventsFilter filter = new TourEventsFilter();
        when(tourDao.findWithEvents(1, filter)).thenReturn(tourWithEvents.entrySet().iterator().next());

        try {
            tourService.getTour(1L, filter);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.TOUR_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void findToursOK() {

        List<TourRecord> tours = new ArrayList<>();
        TourRecord tourRecord = buildTourRecord();
        tours.add(tourRecord);

        when(tourDao.countByFilter(any(ToursFilter.class))).thenReturn(1L);
        when(tourDao.find(any(ToursFilter.class))).thenReturn(tours);

        ToursFilter filter = new ToursFilter();
        filter.setOperatorId(1L);
        filter.setEntityId(2L);
        ToursDTO dto = tourService.findTours(filter);

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getMetadata());
        Assertions.assertNotNull(dto.getData());
        Assertions.assertEquals(1, dto.getData().size());
        BaseTourDTO tourDTO = dto.getData().get(0);
        Assertions.assertNotNull(tourDTO);
        Assertions.assertEquals(tourRecord.getIdgira().longValue(), tourDTO.getId().longValue());
        Assertions.assertNotNull(tourDTO.getStatus());
        Assertions.assertEquals(tourRecord.getEstado(), tourDTO.getStatus().getId());
    }

    @Test
    public void findTours_withoutOperator() {
        ToursFilter filter = new ToursFilter();
        filter.setEntityId(1L);

        try {
            tourService.findTours(filter);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(CoreErrorCode.BAD_PARAMETER.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void createTourOK() {
        when(tourDao.countByNameAndEntity(any(), any())).thenReturn(0L);
        when(tourDao.insert(any())).thenReturn(buildTourRecord());

        BaseTourDTO tourDTO = ObjectRandomizer.random(BaseTourDTO.class);
        tourService.createTour(tourDTO);

        verify(tourDao, times(1)).insert(any());
    }

    @Test
    public void createTourOK_repeatedName() {
        when(tourDao.countByNameAndEntity(any(), any())).thenReturn(1L);

        BaseTourDTO tourDTO = ObjectRandomizer.random(BaseTourDTO.class);
        try {
            tourService.createTour(tourDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.INVALID_NAME_CONFLICT.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void updateTourOK() {
        TourRecord tourRecord = new TourRecord();
        tourRecord.setEstado(TourStatus.INACTIVE.getId());
        when(tourDao.find(anyInt())).thenReturn(tourRecord);
        when(tourDao.countByNameAndEntity(any(), any())).thenReturn(0L);

        BaseTourDTO tourDTO = ObjectRandomizer.random(BaseTourDTO.class);
        tourDTO.setStatus(TourStatus.ACTIVE);
        tourService.updateTour(1L, tourDTO);

        Mockito.doAnswer(a -> {
            CpanelGiraRecord tourUpdate = (CpanelGiraRecord) a.getArguments()[0];
            Assertions.assertEquals(TourStatus.ACTIVE.getId(), tourUpdate.getEstado());
            return Void.class;
        }).when(tourDao).update(any());
    }

    @Test
    public void findCommunicationElementsByTourId_ok() {
        TourRecord tourRecord = new TourRecord();
        tourRecord.setEstado(TourStatus.ACTIVE.getId());
        when(tourDao.find(anyInt())).thenReturn(tourRecord);

        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setValor("value");
        record.setDestino(1);
        record.setIdioma(2);
        record.setIdelemento(1);
        record.setIdtag(1);
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);
        when(communicationElementDao.findCommunicationElements(any(), any(), anyLong(), any()))
                .thenReturn(records);
        when(staticDataContainer.getLanguage(any())).thenReturn("language");
        when(staticDataContainer.getTagId(any())).thenReturn(1);
        List<EventCommunicationElementDTO> elems = tourService.findCommunicationElements(1L, new EventCommunicationElementFilter());
        assertEquals(EventCommunicationElementConverter.fromRecords(records, tourRecord, staticDataContainer), elems);
    }

    @Test
    public void findCommunicationElementsByTourId_invalidTourId() {
        Assertions.assertThrows(OneboxRestException.class, () ->
                tourService.findCommunicationElements(-1L, new EventCommunicationElementFilter()));
    }

    @Test
    public void updateCommunicationElementsByTourId_ok() {
        TourRecord tourRecord = new TourRecord();
        tourRecord.setEstado(TourStatus.ACTIVE.getId());
        when(tourDao.find(anyInt())).thenReturn(tourRecord);

        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setIdelemento(1);
        record.setIdtag(EventTagType.LOGO_WEB.getId());
        record.setIdioma(2);
        record.setValor("value");
        record.setDestino(1);
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);

        when(communicationElementDao.findCommunicationElements(any(), any(), anyLong(), any())).thenReturn(records);
        when(staticDataContainer.getLanguageByCode(eq("language"))).thenReturn(2);
        when(communicationElementDao.insert(any())).thenReturn(new CpanelElementosComEventoRecord());

        //Create items
        List<EventCommunicationElementDTO> elements = ObjectRandomizer.randomListOf(EventCommunicationElementDTO.class, 3);
        elements.forEach(e -> e.setTagId(EventTagType.TEXT_TITLE_WEB.getId()));

        //Update existing for key <tagId - lang - position>
        EventCommunicationElementDTO element = new EventCommunicationElementDTO();
        element.setTagId(EventTagType.LOGO_WEB.getId());
        element.setLanguage("language");
        element.setValue("value mod");

        List<EventCommunicationElementDTO> elementsResult = new ArrayList<>(elements);
        elementsResult.add(element);

        tourService.updateCommunicationElements(1L, elementsResult);

        verify(communicationElementDao, times(3)).insert(any());
        verify(communicationElementDao, times(4)).update(any());
    }

    @Test
    public void postUpdateEvent() {
        List<Long> events = Arrays.asList(1L, 2L);
        when(tourDao.findTourEvents(anyInt())).thenReturn(events);
        doNothing().when(refreshDataService).refreshEvent(anyLong(), anyString());

        tourService.postUpdateTourEvents(1L, null);
        verify(refreshDataService, times(2)).refreshEvent(anyLong(), anyString(), isNull(EventIndexationType.class));
    }

    private TourRecord buildTourRecord() {
        TourRecord tour = new TourRecord();
        tour.setIdgira(1);
        tour.setIdentidad(1);
        tour.setEstado(2);
        return tour;
    }
}
