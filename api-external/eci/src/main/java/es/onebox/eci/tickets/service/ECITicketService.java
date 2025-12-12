package es.onebox.eci.tickets.service;

import es.onebox.common.amt.AMTCustomTag;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.ms.order.dto.AttendantHistoryDTO;
import es.onebox.common.datasources.ms.order.dto.AttendantHistoryRecordDTO;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductAction;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductRequest;
import es.onebox.common.datasources.ms.order.enums.OrderActionTypeSupport;
import es.onebox.common.datasources.ms.order.enums.TicketFormat;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.common.datasources.orders.enums.OrderDetailsItemState;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.tickets.TicketGenerationSupport;
import es.onebox.common.tickets.dto.OrderPrintDTO;
import es.onebox.common.tickets.dto.OrderPrintRequest;
import es.onebox.common.utils.Utilities;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.eci.config.EciScopeConfiguration;
import es.onebox.eci.tickets.ECITicketMessage;
import es.onebox.eci.utils.AuthenticationUtils;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.tracer.core.AMT;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class ECITicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ECITicketService.class);

    private static final String AUTH_VENDOR_TPV = "ECITPV";

    private final TicketGenerationSupport ticketGenerationSupport;
    private final DefaultProducer eciTicketProducer;
    private final UsersRepository usersRepository;
    private final MsOrderRepository msOrderRepository;
    private final AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    private final EciScopeConfiguration eciScopeConfiguration;

    @Autowired
    public ECITicketService(TicketGenerationSupport ticketGenerationSupport,
                            @Qualifier("eciTicketProducer") DefaultProducer eciTicketProducer,
                            UsersRepository usersRepository,
                            MsOrderRepository msOrderRepository,
                            AuthVendorChannelConfigRepository authVendorChannelConfigRepository,
                            EciScopeConfiguration eciScopeConfiguration) {
        this.ticketGenerationSupport = ticketGenerationSupport;
        this.eciTicketProducer = eciTicketProducer;
        this.usersRepository = usersRepository;
        this.msOrderRepository = msOrderRepository;
        this.authVendorChannelConfigRepository = authVendorChannelConfigRepository;
        this.eciScopeConfiguration = eciScopeConfiguration;
    }

    public OrderPrintDTO print(String code, OrderPrintRequest request) {
        if (isNull(code) || code.isEmpty()) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "order cannot be null", null);
        }
        AMT.addTracingAndAuditProperty(AMTCustomTag.ORDER_CODE.value(), code);

        OrderPrintDTO orderPrintDTO = null;

        if (ticketGenerationSupport.processReady(code)) {
            return orderPrintDTO;
        }

        UserAuthentication userAuthentication = AuthenticationUtils.getUserAuthentication();
        AuthVendorConfig authVendorConfig = authVendorChannelConfigRepository.getAuthVendorConfiguration(AUTH_VENDOR_TPV);
        User userDTO = usersRepository.getByUsername(userAuthentication.getUser(), eciScopeConfiguration.getOperatorId());

        OrderDTO orderDTO = msOrderRepository.getOrderByCode(code);

        if (!checkVisibility(orderDTO, authVendorConfig, userDTO)) {
            throw new OneboxRestException(ApiExternalErrorCode.NOT_FOUND, "order not found", null);
        }

        Boolean isTotalRefund = orderDTO.getProducts().stream().allMatch(item -> item.getRelatedRefundCode() != null && OrderDetailsItemState.REFUNDED.name().equals(item.getRelatedRefundCode()));
        if (BooleanUtils.isTrue(isTotalRefund)) {
            LOGGER.info("[ECI TICKET] Order detail response: {}", Utilities.serializeJson(orderDTO));
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_STATE, "This order is totally refunded", null);
        }

        Boolean isPartialRefund = orderDTO.getProducts().stream().anyMatch(item -> item.getRelatedRefundCode() != null && OrderDetailsItemState.REFUNDED.name().equals(item.getRelatedRefundCode()));
        String mergedTickets = ticketGenerationSupport.getMergedTickets(orderDTO, null);

        if (isNull(mergedTickets) || shouldRegenerate(orderDTO)) {
            ECITicketMessage eCITicketMessage = new ECITicketMessage();
            eCITicketMessage.setCode(code);
            eCITicketMessage.setUserId(userDTO.getId());
            try {
                eciTicketProducer.sendMessage(eCITicketMessage);
                LOGGER.info("[ECI TICKET] -> The ticket for order Code {} has been queued", orderDTO.getCode());
            } catch (Exception e) {
                ticketGenerationSupport.removeSemaphore(orderDTO.getCode());
                throw new OneboxRestException(e);
            }
        } else if (BooleanUtils.isTrue(isPartialRefund)) {
            List<OrderProductDTO> itemsList = orderDTO.getProducts().stream().filter(i -> !OrderDetailsItemState.REFUNDED.name().equals(i.getRelatedRefundCode())).collect(Collectors.toList());
            ticketGenerationSupport.joinTicketsPDF(orderDTO, itemsList, null);
            mergedTickets = ticketGenerationSupport.getMergedTickets(orderDTO, null);
            orderPrintDTO = fillOrderPrintResponse(orderDTO, mergedTickets, request, userAuthentication.getUser());
        } else {
            orderPrintDTO = fillOrderPrintResponse(orderDTO, mergedTickets, request, userAuthentication.getUser());
        }

        return orderPrintDTO;
    }

    private OrderPrintDTO fillOrderPrintResponse(OrderDTO orderDTO, String mergedTickets, OrderPrintRequest request, String username) {
        OrderPrintDTO orderPrintDTO = new OrderPrintDTO();
        orderPrintDTO.setCode(orderDTO.getCode());
        orderPrintDTO.setDownloadLink(mergedTickets);

        List<Long> items = orderDTO.getProducts().stream()
                .filter(item -> item.getRelatedRefundCode() == null)
                .map(OrderProductDTO::getId)
                .toList();

        Long entityId = orderDTO.getOrderData().getChannelEntityId().longValue();
        User user = usersRepository.getByUsername(username, ticketGenerationSupport.getOperatorId(entityId));

        OrderProductRequest orderProductRequest = new OrderProductRequest();
        orderProductRequest.setUserId(user.getId());
        orderProductRequest.setType(OrderActionTypeSupport.PRINT);
        orderProductRequest.setFormat(TicketFormat.HARD_TICKET);

        if (nonNull(request)) {
            orderProductRequest.setChannelId(request.getChannelId().longValue());
            orderProductRequest.setCustomAttrs(nonNull(request.getExternalAttributes())
                    ? request.getExternalAttributes() : new HashMap<>());
        }

        orderProductRequest.setProductsId(items);

        msOrderRepository.upsertOrderAction(orderDTO.getCode(), orderProductRequest);

        return orderPrintDTO;
    }

    private boolean checkVisibility(OrderDTO orderDTO, AuthVendorConfig authVendorConfig, User user) {
        return nonNull(orderDTO) && (eciScopeConfiguration.getOperatorId().equals(user.getEntityId()) ||
                (eciScopeConfiguration.getEntities().contains(user.getEntityId()) &&
                        authVendorConfig.getPostSalesConfig().getScope().getChannels().contains(orderDTO.getOrderData().getChannelId())) ||
                orderDTO.getOrderData().getChannelEntityId().equals(user.getEntityId().intValue()));
    }

    private boolean shouldRegenerate(OrderDTO orderDTO) {
        return isAttendantModified(orderDTO) || isRelocated(orderDTO);
    }

    private static Boolean isRelocated(OrderDTO orderDTO) {
        return orderDTO.getProducts().stream()
                .anyMatch(p -> BooleanUtils.isTrue(p.getOperations().getRelocated()) &&
                        shouldGenerate(p.getOperations().getActions()));
    }

    public static boolean shouldGenerate(List<OrderProductAction> actions) {

        ZonedDateTime lastRelocated = actions.stream()
                .filter(a -> a.getType() == OrderActionTypeSupport.RELOCATED)
                .map(OrderProductAction::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (lastRelocated == null) {
            return false;
        }

        ZonedDateTime lastGenerated = actions.stream()
                .filter(a ->OrderActionTypeSupport.GENERATED.equals(a.getType()) &&
                        TicketFormat.HARD_TICKET.equals(a.getFormat()))
                .map(OrderProductAction::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (lastGenerated == null) {
            return true;
        }

        return lastRelocated.isAfter(lastGenerated);
    }

    private boolean isAttendantModified(OrderDTO orderDTO) {
        ZonedDateTime lastAttendantModification = orderDTO.getProducts().stream()
                .map(OrderProductDTO::getAttendant)
                .filter(Objects::nonNull)
                .map(AttendantHistoryDTO::getAttendantHistory)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(AttendantHistoryRecordDTO::getModificationDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);

        ZonedDateTime creationDate = ticketGenerationSupport.getFileCreationDate(orderDTO);

        return lastAttendantModification != null && creationDate != null
                && lastAttendantModification.isAfter(creationDate);
    }
}
