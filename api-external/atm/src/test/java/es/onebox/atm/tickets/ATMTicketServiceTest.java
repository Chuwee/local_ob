package es.onebox.atm.tickets;

import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.tickets.TicketGenerationSupport;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

class ATMTicketServiceTest {

    @Value("${atm.tickets.download-endpoint}")
    private String ticketDownloadUrl;
    @Value("${atm.entity.entityId}")
    private Long atmEntityId;

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private DefaultProducer atmTicketProducer;
    @Mock
    private TicketGenerationSupport ticketGenerationSupport;

    private static MockedStatic<AuthenticationUtils> authenticationUtils;

    @InjectMocks
    private ATMTicketService atmTicketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        authenticationUtils.close();
    }


    @Test
    void getTicketsURLContent() {


    }
}