package es.onebox.fifaqatar.adapter;

import es.onebox.fifaqatar.adapter.mapping.CustomerMappingCouchDao;
import es.onebox.fifaqatar.adapter.mapping.IdGenerator;
import es.onebox.fifaqatar.adapter.mapping.OrderDetailMappingCouchDao;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMapping;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMappingCouchDao;
import org.springframework.stereotype.Component;

@Component
public class FifaQatarMappingHelper {

    private final OrderDetailMappingCouchDao orderDetailMappingCouchDao;
    private final TicketDetailMappingCouchDao ticketDetailMappingCouchDao;
    private final CustomerMappingCouchDao customerMappingCouchDao;

    public FifaQatarMappingHelper(OrderDetailMappingCouchDao orderDetailMappingCouchDao, TicketDetailMappingCouchDao ticketDetailMappingCouchDao, CustomerMappingCouchDao customerMappingCouchDao) {
        this.orderDetailMappingCouchDao = orderDetailMappingCouchDao;
        this.ticketDetailMappingCouchDao = ticketDetailMappingCouchDao;
        this.customerMappingCouchDao = customerMappingCouchDao;
    }

    public int createTicketDetailMapping(String orderCode, Long sessionId) {
        int ticketId = IdGenerator.generateRandomId();

        TicketDetailMapping ticketDetailMapping = new TicketDetailMapping();
        ticketDetailMapping.setId(String.valueOf(ticketId));
        ticketDetailMapping.setOrderCode(orderCode);
        ticketDetailMapping.setSessionId(sessionId);

        ticketDetailMappingCouchDao.upsert(String.valueOf(ticketId), ticketDetailMapping);
        ticketDetailMappingCouchDao.upsert(orderCode + "_" + sessionId, ticketDetailMapping);

        return ticketId;
    }

    public TicketDetailMapping getByTicketId(Integer ticketId) {
        return ticketDetailMappingCouchDao.get(String.valueOf(ticketId));
    }

    public TicketDetailMapping getByOrderCodeAndSessionId(String orderCode, Long sessionId) {
        return ticketDetailMappingCouchDao.get(orderCode + "_" + sessionId);
    }

    public int createOrderDetailMapping(String orderCode) {
        int orderId = IdGenerator.generateRandomId();

        orderDetailMappingCouchDao.upsert(String.valueOf(orderId), orderCode);
        orderDetailMappingCouchDao.upsert(orderCode, String.valueOf(orderId));

        return orderId;
    }

    public Integer getOrderMappingByOrderCode(String orderCode) {
        String orderId = orderDetailMappingCouchDao.get(orderCode);

        return orderId != null ? Integer.parseInt(orderId) : null;
    }

    public String getByOrderId(Integer orderId) {
        return orderDetailMappingCouchDao.get(String.valueOf(orderId));
    }

    public String getCustomerIdByOriginId(Integer originId) {
        return customerMappingCouchDao.get(String.valueOf(originId));
    }

    public void createCustomerMapping(Integer originId, String customerId) {
        customerMappingCouchDao.upsert(String.valueOf(originId), customerId);
    }
}
