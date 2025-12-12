package es.onebox.bepass.tickets.service;

import es.onebox.bepass.common.BepassEntityService;
import es.onebox.bepass.datasources.bepass.dto.CreateTicketResponse;
import es.onebox.bepass.datasources.bepass.dto.Ticket;
import es.onebox.bepass.datasources.bepass.dto.TicketsResponse;
import es.onebox.bepass.datasources.bepass.repository.BepassTicketsRepository;
import es.onebox.bepass.events.BepassEventsService;
import es.onebox.bepass.tickets.converter.TicketsConverter;
import es.onebox.bepass.tickets.dto.NotificationMessageDTO;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.enums.OrderState;
import es.onebox.common.datasources.ms.order.enums.OrderType;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.EncryptionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class BepassTicketsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BepassTicketsService.class);

    private final MsOrderRepository msOrderRepository;
    private final MsEventRepository eventRepository;
    private final CustomerRepository customerRepository;
    private final BepassEventsService bepassEventsService;
    private final BepassTicketsRepository bepassTicketsRepository;
    private final BepassEntityService bepassEntityService;
    private final EncryptionUtils encryptionUtils;

    public BepassTicketsService(MsOrderRepository msOrderRepository,
                                MsEventRepository eventRepository,
                                BepassEventsService bepassEventsService,
                                CustomerRepository customerRepository,
                                BepassTicketsRepository bepassTicketsRepository,
                                BepassEntityService bepassEntityService,
                                EncryptionUtils encryptionUtils) {
        this.msOrderRepository = msOrderRepository;
        this.bepassEventsService = bepassEventsService;
        this.eventRepository = eventRepository;
        this.customerRepository = customerRepository;
        this.bepassTicketsRepository = bepassTicketsRepository;
        this.bepassEntityService = bepassEntityService;
        this.encryptionUtils = encryptionUtils;
    }

    public void sendTicket(String action, String event, NotificationMessageDTO body) {
        String orderCode = body.getCode();
        LOGGER.info("[BEPASS WEBHOOK][{}][{}] Received notification for order: {}", event, action, orderCode);
        if (orderCode == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        OrderDTO order = msOrderRepository.getOrderByCode(orderCode);
        validateOrderData(order);
        bepassEntityService.initContext(order);

        OrderType type = order.getStatus().getType();
        OrderState state = order.getStatus().getState();
        if (!OrderType.REFUND.equals(type) && order.getProducts().stream().anyMatch(p -> p.getRelatedRefundCode() != null)) {
            return;
        }
        if (OrderType.PURCHASE.equals(type) && !OrderState.CANCELLED.equals(state)) {
            createTickets(order, orderCode);
        } else if (OrderType.REFUND.equals(type) || OrderState.CANCELLED.equals(state)) {
            refundTickets(order, orderCode);
        } else {
            LOGGER.error("[BEPASS WEBHOOK] Invalid order type: {}", orderCode);
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_TYPE);
        }
    }

    private void createTickets(OrderDTO order, String orderCode) {
        try {
            createOrUpdateTickets(order);
        } catch (Exception e) {
            LOGGER.error("[BEPASS WEBHOOK] Error creating or updating tickets for order {}: {}", orderCode, e.getMessage());
            throw new OneboxRestException(ApiExternalErrorCode.BEPASS_TICKET_CREATION_FAILED);
        }
    }

    private void refundTickets(OrderDTO order, String orderCode) {
        try {
            this.refundTickets(order);
        } catch (Exception e) {
            LOGGER.error("[BEPASS WEBHOOK] Error deactivating tickets for order {}: {}", orderCode, e.getMessage());
            throw new OneboxRestException(ApiExternalErrorCode.BEPASS_TICKET_DEACTIVATION_FAILED);
        }
    }

    private void createOrUpdateTickets(OrderDTO order) {
        String userId = order.getCustomer().getUserId();
        Customer customer = this.customerRepository.getCustomer(userId);

        CreateTicketResponse response = this.createTickets(customer, order);

        if (response != null && CollectionUtils.isNotEmpty(response.getTicketsAlreadyInDataBase())) {
            Set<Long> ids = response.getTicketsAlreadyInDataBase()
                    .stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            List<OrderProductDTO> products = order.getProducts().stream()
                    .filter(p -> ids.contains(p.getId()))
                    .toList();
            this.updateExistingProducts(customer, products);
        }
    }

    public CreateTicketResponse sendTicket(String orderCode) {
        OrderDTO order = msOrderRepository.getOrderByCode(orderCode);
        OrderType type = order.getStatus().getType();
        OrderState state = order.getStatus().getState();
        if (OrderType.PURCHASE.equals(type) && !OrderState.CANCELLED.equals(state)) {
            String userId = order.getCustomer().getUserId();
            Customer customer = this.customerRepository.getCustomer(userId);
            return this.createTickets(customer, order);
        } else if (OrderType.REFUND.equals(type) || OrderState.CANCELLED.equals(state)) {
            this.refundTickets(order);
        }
        throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_TYPE);
    }

    public CreateTicketResponse createTickets(Customer customer, OrderDTO order) {
        var productsBySession = order.getProducts().stream().filter(s -> ProductType.SEAT.equals(s.getType()))
                .collect(groupingBy(OrderProductDTO::getSessionId, Collectors.toList()));

        if (MapUtils.isEmpty(productsBySession)) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_TYPE);
        }

        List<Ticket> tickets = new ArrayList<>();
        productsBySession.forEach((sessionId, products) -> {
            SessionDTO session = eventRepository.getSession(Long.valueOf(sessionId));
            String externalEventId = bepassEventsService.extractOrCreateExternalEvent(session);
            tickets.addAll(TicketsConverter.toCreate(customer, products, externalEventId, session, encryptionUtils::decode));
        });
        return bepassTicketsRepository.addTicket(tickets);
    }

    public void updateExistingProducts(Customer customer, List<OrderProductDTO> existingProducts) {
        var productsBySession = existingProducts.stream().filter(s -> ProductType.SEAT.equals(s.getType()))
                .collect(groupingBy(OrderProductDTO::getSessionId, Collectors.toList()));

        productsBySession.forEach((sessionId, products) -> {
            SessionDTO session = eventRepository.getSession(Long.valueOf(sessionId));
            String externalEventId = bepassEventsService.extractOrCreateExternalEvent(session);
            products.forEach(product -> {
                Ticket updatedTicket = TicketsConverter.toCreate(customer, product, externalEventId, session, encryptionUtils::decode);
                bepassTicketsRepository.updateTicket(updatedTicket);
            });
        });
    }

    public void refundTickets(OrderDTO order) {
        var productsBySession = order.getProducts().stream().filter(s -> ProductType.SEAT.equals(s.getType()))
                .collect(groupingBy(OrderProductDTO::getSessionId, Collectors.toList()));
        OrderType type = order.getStatus().getType();
        OrderState state = order.getStatus().getState();
        if (!OrderType.REFUND.equals(type) && !OrderState.CANCELLED.equals(state)) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_TYPE);
        }
        productsBySession.forEach((sessionId, products) -> {
            SessionDTO session = eventRepository.getSession(Long.valueOf(sessionId));
            String externalEventId = bepassEventsService.extractOrCreateExternalEvent(session);
            products.forEach(product -> {
                Ticket refund = TicketsConverter.toRefund(product, externalEventId, encryptionUtils::decode);
                bepassTicketsRepository.updateTicket(refund);
            });
        });
    }

    public TicketsResponse searchTicketsByEvent(String eventId, Long page) {
        return this.bepassTicketsRepository.getEventTickets(eventId, Optional.ofNullable(page).orElse(1L));
    }

    public TicketsResponse searchTickets(Long page) {
        return this.bepassTicketsRepository.getRawTickets(Optional.ofNullable(page).orElse(1L));
    }

    private void validateOrderData(OrderDTO order) {
        if (order == null || order.getStatus() == null
                || order.getOrderData() == null || order.getOrderData().getChannelEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
    }


}
