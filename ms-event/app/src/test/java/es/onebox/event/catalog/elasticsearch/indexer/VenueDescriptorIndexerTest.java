package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.dao.VenueDescriptorCouchDao;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class VenueDescriptorIndexerTest {

    private static final int EVENT_ID = 877;

    @Mock
    private VenueDescriptorCouchDao venueDescriptorCouchDao;

    private VenueDescriptorIndexer venueDescriptorIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.venueDescriptorIndexer = new VenueDescriptorIndexer(venueDescriptorCouchDao);
    }

    @Test
    void testIndexVenueDescriptors_whenPartialBasic_shouldNotIndex() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
        venueDescriptorIndexer.indexVenueDescriptors(context);
        verifyNoInteractions(venueDescriptorCouchDao);
    }

    @Test
    void testIndexVenueDescriptors_whenPartialComElements_shouldNotIndex() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_COM_ELEMENTS);
        venueDescriptorIndexer.indexVenueDescriptors(context);
        verifyNoInteractions(venueDescriptorCouchDao);
    }

    @Test
    void testIndexVenueDescriptors_whenFullIndexation_shouldIndex() {
        EventIndexationContext context = buildContext(EventIndexationType.FULL);
        Map<Integer, VenueDescriptor> venueDescriptor = new HashMap<>();
        venueDescriptor.put(1, new VenueDescriptor());
        context.setVenueDescriptor(venueDescriptor);
        venueDescriptorIndexer.indexVenueDescriptors(context);
        verify(venueDescriptorCouchDao).bulkUpsert(any());
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext context = new EventIndexationContext(eventRecord, type);
        context.setVenueDescriptor(new HashMap<>());
        return context;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        return event;
    }
} 
