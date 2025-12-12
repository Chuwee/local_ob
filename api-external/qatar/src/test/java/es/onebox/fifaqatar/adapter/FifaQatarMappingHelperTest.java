package es.onebox.fifaqatar.adapter;

import es.onebox.fifaqatar.adapter.mapping.CustomerMappingCouchDao;
import es.onebox.fifaqatar.adapter.mapping.OrderDetailMappingCouchDao;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMapping;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMappingCouchDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class FifaQatarMappingHelperTest {

    @Mock
    private OrderDetailMappingCouchDao orderDetailMappingCouchDao;
    @Mock
    private TicketDetailMappingCouchDao ticketDetailMappingCouchDao;
    @Mock
    private CustomerMappingCouchDao customerMappingCouchDao;

    @InjectMocks
    private FifaQatarMappingHelper fifaQatarMappingHelper;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_whenGetTicketId_thenReturnOk() {
        final Integer TICKET_ID = 100;
        final String ORDER_CODE = "AAA";
        final Long SESSION_ID = 1L;

        var response = new TicketDetailMapping();
        response.setId(String.valueOf(TICKET_ID));
        response.setOrderCode(ORDER_CODE);
        response.setSessionId(SESSION_ID);

        when(ticketDetailMappingCouchDao.get(String.valueOf(TICKET_ID))).thenReturn(response);

        TicketDetailMapping byTicketId = fifaQatarMappingHelper.getByTicketId(TICKET_ID);

        assertEquals(ORDER_CODE, byTicketId.getOrderCode());
        assertEquals(SESSION_ID, byTicketId.getSessionId());
        assertEquals(String.valueOf(TICKET_ID), byTicketId.getId());
    }

    @Test
    public void test_whenGetOrderId_thenReturnOk() {
        final Integer ORDER_ID = 111;
        final String ORDER_CODE = "CODE";

        when(orderDetailMappingCouchDao.get(String.valueOf(ORDER_ID))).thenReturn(ORDER_CODE);

        String byOrderId = fifaQatarMappingHelper.getByOrderId(ORDER_ID);
        assertEquals(ORDER_CODE, byOrderId);
    }

    @Test
    public void test_whenGetByOrderCode_thenOk() {
        final String ORER_CODE = "CODE";
        final Integer ORDER_ID = 100;

        when(orderDetailMappingCouchDao.get(ORER_CODE)).thenReturn(String.valueOf(ORDER_ID));
        when(orderDetailMappingCouchDao.get("NOPE")).thenReturn(null);

        assertEquals(ORDER_ID, fifaQatarMappingHelper.getOrderMappingByOrderCode(ORER_CODE));
        assertNull(fifaQatarMappingHelper.getOrderMappingByOrderCode("NOPE"));
    }

    @Test
    public void test_customerMapping() {
        when(customerMappingCouchDao.get(String.valueOf(1))).thenReturn("ID");
        when(customerMappingCouchDao.get(String.valueOf(2))).thenReturn(null);

        assertEquals("ID", fifaQatarMappingHelper.getCustomerIdByOriginId(1));
        assertNull(fifaQatarMappingHelper.getCustomerIdByOriginId(2));

    }
}
