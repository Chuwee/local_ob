package es.onebox.event.common;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommonCommunicationElementServiceTest {

    private static final String ENTITY_NAME = "entity";

    @Mock
    private EventDao eventDao;
    @Mock
    private EventCommunicationElementDao communicationElementDao;
    @Mock
    private StaticDataContainer staticDataContainer;
    @Mock
    private S3BinaryRepository s3BinaryRepository;
    @InjectMocks
    private CommonCommunicationElementService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findCommunicationElementsByEventId_ok() {
        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setPosition(1);
        record.setValor("value");
        record.setDestino(1);
        record.setIdioma(2);
        record.setIdelemento(1);
        record.setIdtag(1);
        record.setAlttext("alttext");
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);
        when(communicationElementDao.findCommunicationElements(anyLong(), any(), any(), any()))
                .thenReturn(records);
        when(staticDataContainer.getLanguage(any())).thenReturn("language");
        when(staticDataContainer.getTagId(any())).thenReturn(1);
        Map.Entry<EventRecord, List<VenueRecord>> event = createEventArray().entrySet().iterator().next();
        EventRecord eventRecord = event.getKey();

        List<EventCommunicationElementDTO> elems = service.findCommunicationElements(1L, new EventCommunicationElementFilter(), eventRecord);
        assertEquals(EventCommunicationElementConverter.fromRecords(records, event.getKey(), staticDataContainer), elems);
    }

    @Test
    public void updateCommunicationElementsByEventId_ok() {
        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setIdelemento(1);
        record.setIdtag(EventTagType.IMG_BODY_WEB.getId());
        record.setIdioma(2);
        record.setPosition(1);
        record.setValor("value");
        record.setDestino(1);
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);

        when(communicationElementDao.findCommunicationElements(anyLong(), any(), any(), any())).thenReturn(records);
        when(staticDataContainer.getLanguageByCode(eq("language"))).thenReturn(2);
        when(communicationElementDao.insert(any())).thenReturn(new CpanelElementosComEventoRecord());

        //Create items
        List<EventCommunicationElementDTO> elements = ObjectRandomizer.randomListOf(EventCommunicationElementDTO.class, 3);
        elements.forEach(e -> e.setTagId(EventTagType.TEXT_TITLE_WEB.getId()));

        //Update existing for key <tagId - lang - position>
        EventCommunicationElementDTO element = new EventCommunicationElementDTO();
        element.setTagId(EventTagType.IMG_BODY_WEB.getId());
        element.setLanguage("language");
        element.setPosition(1);
        element.setValue("value mod");
        element.setAltText("alttext");

        List<EventCommunicationElementDTO> resultRlements = new ArrayList<>(elements);
        resultRlements.add(element);

        EventRecord eventRecord = new EventRecord();
        eventRecord.setOperatorId(1);
        eventRecord.setIdentidad(2);

        service.updateCommunicationElements(1L, resultRlements, eventRecord);

        verify(communicationElementDao, times(3)).insert(any());
        verify(communicationElementDao, times(4)).update(any());
    }

    @Test
    public void updateCommunicationElementsByEventId_uploadImage_ok() {
        CpanelElementosComEventoRecord record = new CpanelElementosComEventoRecord();
        record.setIdelemento(1);
        record.setIdtag(EventTagType.IMG_BODY_WEB.getId());
        record.setIdioma(2);
        record.setPosition(1);
        record.setValor("value");
        record.setDestino(1);
        List<CpanelElementosComEventoRecord> records = Collections.singletonList(record);

        when(communicationElementDao.findCommunicationElements(anyLong(), any(), any(), any())).thenReturn(records);
        when(staticDataContainer.getLanguageByCode(eq("language"))).thenReturn(2);

        //Create items
        List<EventCommunicationElementDTO> elements = new ArrayList<>();

        //Update existing for key <tagId - lang - position>
        EventCommunicationElementDTO element = new EventCommunicationElementDTO();
        element.setTagId(EventTagType.IMG_BODY_WEB.getId());
        element.setLanguage("language");
        element.setPosition(1);
        element.setValue("value mod");
        element.setImageBinary(Optional.of("base64"));
        element.setAltText("alttext");
        elements.add(element);

        EventRecord eventRecord = new EventRecord();
        eventRecord.setOperatorId(1);
        eventRecord.setIdentidad(2);

        service.updateCommunicationElements(1L, elements, eventRecord);

        verify(communicationElementDao, times(1)).update(any());
        verify(s3BinaryRepository, times(1)).delete(any());
        verify(s3BinaryRepository, times(1)).upload(any(), any());
    }

    private Map<EventRecord, List<VenueRecord>> createEventArray() {
        Map<EventRecord, List<VenueRecord>> events = new HashMap<>();
        events.put(createEventMapper(), null);
        return events;
    }

    private EventRecord createEventMapper() {
        EventRecord event = new EventRecord();

        event.setIdevento(52);
        event.setTipoevento(EventType.NORMAL.getId());
        event.setEstado(EventStatus.READY.getId());
        event.setNombre("Event");
        event.setIdentidad(1);
        event.setOperatorId(1);
        event.setEntityName(ENTITY_NAME);
        event.setReferenciapromotor(random(String.class));
        event.setFechainicio(Timestamp.valueOf("2018-10-01 10:00:00"));
        event.setFechafin(Timestamp.valueOf("2018-10-01 11:00:00"));
        event.setFechainiciotz(1);
        event.setFechafintz(1);
        event.setStartDateTZ("Europe/Berlin");
        event.setEndDateTZ("Europe/Berlin");
        event.setStartDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        event.setEndDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        event.setStartDateTZOffset(60);
        event.setEndDateTZOffset(60);
        event.setEmailresponsable(random(String.class));
        event.setNombreresponsable(random(String.class));
        event.setApellidosresponsable(random(String.class));
        event.setTelefonoresponsable(random(String.class));
        event.setObjetivosobreentradas(random(Integer.class));
        event.setObjetivosobreventas(random(Double.class));
        event.setIdpromotor(random(Integer.class));
        event.setPromoterName(random(String.class));
        event.setIdtaxonomia(Math.abs(random(Integer.class)));
        event.setCategoryCode(random(String.class));
        event.setCategoryDescription(random(String.class));
        event.setIdtaxonomiapropia(Math.abs(random(Integer.class)));
        event.setCustomCategoryRef(random(String.class));
        event.setCustomCategoryDescription(random(String.class));
        event.setIdgira(random(Integer.class));
        event.setTourName(random(String.class));
        return event;
    }
}
