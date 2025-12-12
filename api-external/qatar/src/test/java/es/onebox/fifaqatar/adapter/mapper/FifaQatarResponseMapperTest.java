package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.dto.Coordinates;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.enums.OrderItemRelatedProductState;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.fifaqatar.BaseTest;
import es.onebox.fifaqatar.adapter.FifaQatarMappingHelper;
import es.onebox.fifaqatar.adapter.dto.response.TicketsResponse;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketDetailResponse;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class FifaQatarResponseMapperTest extends BaseTest {

    @Mock
    private MsEventRepository msEventRepository;
    @Mock
    private VenueTemplateRepository venueTemplateRepository;
    @Mock
    private FifaQatarMappingHelper fifaQatarMappingHelper;
    @Mock
    private MapperContext mapperContext;

    @Spy
    @InjectMocks
    private FifaQatarResponseMapper fifaQatarResponseMapper;

    final long EVENT_ID = 90567L;
    final long SESSION_ID = 636323L;
    final long VENUE_ID = 15560L;
    final String CUSTOMER_ID = "Z2A9QN3IX7";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(msEventRepository.getEventCatalog(EVENT_ID)).thenReturn(mockedEventCatalog(EVENT_ID));
        when(msEventRepository.getSessionCatalog(SESSION_ID)).thenReturn(mockedSessionCatalog(EVENT_ID, SESSION_ID));
        when(venueTemplateRepository.getVenue(VENUE_ID)).thenReturn(mockedVenue(VENUE_ID));

        when(mapperContext.getCurrentCustomer()).thenReturn(mockedCustomer(CUSTOMER_ID));
        when(mapperContext.getEventCatalog()).thenReturn(mockedEventCatalog(EVENT_ID));
        when(mapperContext.getSessionCatalog()).thenReturn(mockedSessionCatalog(EVENT_ID, SESSION_ID));

        when(mapperContext.getDictionary()).thenReturn(new FifaQatarTranslation());
        when(mapperContext.getAccountProfileUrl()).thenReturn("https://test.com");
        when(mapperContext.getAccountTicketsUrl()).thenReturn("https://test.com");
        when(mapperContext.getCustomerAccessToken()).thenReturn("TOKEN");
        when(mapperContext.getBarcodeUrl()).thenReturn("https://test.com");
        when(mapperContext.getCurrentLang()).thenReturn("en-GB");

        FifaQatarConfigDocument document = Mockito.mock(FifaQatarConfigDocument.class);

        when(mapperContext.getMainConfig()).thenReturn(document);
        when(mapperContext.getMainConfig().getMaxBarcodesByTicketDetail()).thenReturn(50);
    }

    @Test
    void test_emptyItems() {
        TicketsResponse ticketsResponse = fifaQatarResponseMapper.map(List.of(), null);

        assertEquals(0, ticketsResponse.getCount());
        assertEquals(0, ticketsResponse.getResults().size());
    }

    @Test
    void test_ticketsResponse() throws IOException {
        List<OrderItem> orderItems = loadMockedOrderItems("items.json");

        TicketsResponse ticketsResponse = fifaQatarResponseMapper.map(orderItems, mapperContext);

        assertEquals(1, ticketsResponse.getCount());
        assertEquals(1, ticketsResponse.getResults().size());
        assertEquals(1, ticketsResponse.getResults().get(0).getNumTickets());
    }

    @Test
    void test_ticketsResponse_withSecMktSoldItem() throws IOException {
        List<OrderItem> orderItems = loadMockedOrderItems("items.json");
        orderItems.get(0).setRelatedProductState(OrderItemRelatedProductState.SEC_MKT_SOLD);
        TicketsResponse ticketsResponse = fifaQatarResponseMapper.map(orderItems, mapperContext);


        assertEquals(1, ticketsResponse.getCount());
        assertEquals(1, ticketsResponse.getResults().size());
        assertEquals(1, ticketsResponse.getResults().get(0).getNumTickets());
    }

    @Test
    void test_ticketDetail() throws IOException {
        List<OrderItem> orderItems = loadMockedOrderItems("items.json");

        TicketDetailResponse ticketDetailResponse = fifaQatarResponseMapper.mapTicketDetail(orderItems, mapperContext);

        assertEquals(SESSION_ID, ticketDetailResponse.getPlanId().longValue());
        assertEquals(1759312800L, ticketDetailResponse.getSessionStart().toInstant().toEpochMilli());
        assertEquals(1759323600L, ticketDetailResponse.getSessionEnd().minusHours(5).toInstant().toEpochMilli());
    }

    @Test
    void test_ticketDetail_withSecMktSoldItem() throws IOException {
        List<OrderItem> orderItems = loadMockedOrderItems("items.json");
        orderItems.get(0).setRelatedProductState(OrderItemRelatedProductState.SEC_MKT_SOLD);

        doReturn(false).when(fifaQatarResponseMapper).mustFillInfo(any());
        doReturn(false).when(fifaQatarResponseMapper).isDeliveryActive(any());
        doReturn(false).when(fifaQatarResponseMapper).isSessionFinished(any());

        TicketDetailResponse ticketDetailResponse = fifaQatarResponseMapper.mapTicketDetail(orderItems, mapperContext);

        assertEquals(SESSION_ID, ticketDetailResponse.getPlanId().longValue());
        assertEquals(1759312800L, ticketDetailResponse.getSessionStart().toInstant().toEpochMilli());
        assertEquals(1759323600L, ticketDetailResponse.getSessionEnd().minusHours(5).toInstant().toEpochMilli());
        assertEquals(0, ticketDetailResponse.getCodes().size());
    }

    private Customer mockedCustomer(String customerId) {
        Customer dto = new Customer();
        dto.setUserId(customerId);

        return dto;
    }

    private VenueDTO mockedVenue(long venueId) {
        VenueDTO dto = new VenueDTO();
        dto.setId(venueId);
        dto.setCity("El Papiol");
        dto.setAddress("Carrer de la papiola 123");
        dto.setCountry(new IdNameCodeDTO());
        dto.getCountry().setCode("es");
        dto.setCoordinates(new Coordinates());
        dto.getCoordinates().setLatitude(2d);
        dto.getCoordinates().setLongitude(3d);

        return dto;
    }

    private SessionCatalog mockedSessionCatalog(long eventId, long sessionId) {
        SessionCatalog dto = new SessionCatalog();
        dto.setSessionId(sessionId);
        dto.setEventId(eventId);


        // 01/07/2025 10:00:00 UTC
        dto.setBeginSessionDate(1759312800L);
        // 01/07/2025 13:00:00 UTC
        dto.setEndSessionDate(1759323600L);
        dto.setRealEndSessionDate(1759323600L);

        return dto;
    }

    private EventCatalog mockedEventCatalog(long eventId) {
        EventCatalog dto = new EventCatalog();
        dto.setEventId(eventId);

        return dto;
    }
}
