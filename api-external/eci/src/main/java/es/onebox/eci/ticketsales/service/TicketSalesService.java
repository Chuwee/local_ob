package es.onebox.eci.ticketsales.service;

import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.OrderDetailItem;
import es.onebox.common.datasources.orders.dto.OrderPaymentRefund;
import es.onebox.common.datasources.orders.enums.OrderPaymentRefundStatus;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.eci.service.ChannelsHelper;
import es.onebox.eci.ticketsales.converter.TicketSalesConverter;
import es.onebox.eci.ticketsales.dto.Order;
import es.onebox.eci.utils.AuthenticationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class TicketSalesService {

    private final OrdersRepository ordersRepository;
    private final TokenRepository tokenRepository;
    private final ChannelRepository channelRepository;
    private final ChannelsHelper channelsHelper;

    @Autowired
    public TicketSalesService (OrdersRepository ordersRepository,
                               TokenRepository tokenRepository,
                               ChannelRepository channelRepository,
                               ChannelsHelper channelsHelper) {
        this.ordersRepository = ordersRepository;
        this.tokenRepository = tokenRepository;
        this.channelRepository = channelRepository;
        this.channelsHelper = channelsHelper;
    }

    public List<Order> getTicketSales(ZonedDateTime orderGTE, ZonedDateTime orderLTE, String eventId,
                                      ZonedDateTime sessionGTE, ZonedDateTime sessionLTE,
                                      Long limit, Long offset, String channelIdentifier) {
        List<Order> orders = new ArrayList<>();
        List<Long> channelIds = channelsHelper.getChannelIds(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelIds)) {
            orders = getTicketSales(orderGTE, orderLTE, eventId, sessionGTE, sessionLTE, limit, offset, channelIds);
        }
        return orders;
    }

    private List<Order> getTicketSales(ZonedDateTime orderGTE, ZonedDateTime orderLTE, String eventId,
                                      ZonedDateTime sessionGTE, ZonedDateTime sessionLTE,
                                      Long limit, Long offset, List<Long> channelIds) {

        Boolean includeUpdatedRefunds = true;
        UserAuthentication userAuthentication = AuthenticationUtils.getUserAuthentication();

        String token = tokenRepository.getApiOrdersToken(userAuthentication.getUser(), userAuthentication.getPassword());

        List<es.onebox.common.datasources.orders.dto.Order> orders = ordersRepository.getOrders(token, channelIds, orderGTE, orderLTE, includeUpdatedRefunds);
        List<OrderDetail> orderDetails = orders
                .stream()
                .map(order -> ordersRepository.getById(order.getCode(), token))
                .collect(Collectors.toList());

        if (sessionGTE != null || sessionLTE != null) {
            orderDetails = orderDetails.stream()
                    .filter(orderDetail -> hasValidSessionDates(orderDetail, sessionGTE, sessionLTE))
                    .collect(Collectors.toList());
        }

        if (eventId != null) {
            orderDetails = orderDetails.stream()
                    .filter(orderDetail -> hasValidEventId(orderDetail, eventId))
                    .collect(Collectors.toList());
        }

        Map<String, OrderDetail> refundWithOriginal = getRefundWithOriginalOrder(orderDetails, token);

        orderDetails = orderDetails.stream()
                .filter(orderDetail -> ordersWithTransactionId(orderDetail, refundWithOriginal))
                .collect(Collectors.toList());

        orderDetails = orderDetails.stream()
                .sorted(Comparator.comparing(OrderDetail::getDate))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        // add original order when the order is a refund and is not included in 'orderDetails'
        List<OrderDetail> ordersWithOriginals = getOrdersWithOriginals(orderDetails, refundWithOriginal);

        return TicketSalesConverter.convert(ordersWithOriginals, channelRepository::getChannel);
    }



    private boolean hasValidEventId(OrderDetail orderDetail, String eventId) {

        for (OrderDetailItem item : orderDetail.getItems()) {
            if (item.getTicket().getAllocation() != null && item.getTicket().getAllocation().getEvent() != null &&
                    item.getTicket().getAllocation().getEvent().getId().equals(Long.valueOf(eventId))) {
                return true;
            }
        }
        return false;
    }

    private Map<String, OrderDetail> getRefundWithOriginalOrder(List<OrderDetail> orderDetails, String token) {
        Map<String, OrderDetail> refundWithOriginal = new HashMap<>();

        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getType().equals(OrderType.REFUND)) {
                OrderDetailItem orderDetailItem = orderDetail.getItems().stream().findFirst().orElse(null);
                if (orderDetailItem != null) {
                    String originalCode = orderDetailItem.getPreviousOrder().getCode();
                    if (!containOrder(orderDetails, originalCode)) {
                        refundWithOriginal.put(orderDetail.getCode(), getOriginalOrder(originalCode, token));
                    } else {
                        refundWithOriginal.put(orderDetail.getCode(), orderDetails.stream().filter(order -> order.getCode().equals(originalCode)).findFirst().get());
                    }
                }
            }
        }
        return refundWithOriginal;
    }

    private boolean ordersWithTransactionId(OrderDetail orderDetail, Map<String, OrderDetail> refundWithOriginal) {
        if (orderDetail.getType().equals(OrderType.PURCHASE)) {
            return true;
        } else if(orderDetail.getType().equals(OrderType.REFUND)) {
            OrderDetail originalOrder = refundWithOriginal.get(orderDetail.getCode());
            if (nonNull(originalOrder.getPaymentDetail())
                    && nonNull(originalOrder.getPaymentDetail().getReimbursements())) {
                for (OrderPaymentRefund reimbursement : originalOrder.getPaymentDetail().getReimbursements()) {
                    if (orderDetail.getItems().stream().anyMatch(item -> reimbursement.getStatus().equals(OrderPaymentRefundStatus.OK) &&
                            reimbursement.getItemIds().contains(item.getId()) && nonNull(reimbursement.getCustomInfo()) &&
                            nonNull(reimbursement.getCustomInfo().getPgpRefundTransactionId()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<OrderDetail> getOrdersWithOriginals(List<OrderDetail> orderDetails, Map<String, OrderDetail> refundWithOriginal) {
        List<OrderDetail> orders = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
            orders.add(orderDetail);
            if (orderDetail.getType().equals(OrderType.REFUND)) {
                OrderDetail originalOrder = refundWithOriginal.get(orderDetail.getCode());
                if (!containOrder(orderDetails, originalOrder.getCode()) &&
                        orders.stream().noneMatch(order -> order.getCode().equals(originalOrder.getCode()))) {
                    orders.add(originalOrder);
                }
                orderDetail.setPaymentDetail(originalOrder.getPaymentDetail());
            }
        }
        return orders;
    }

    private boolean containOrder(List<OrderDetail> orderDetails, String originalCode) {
        return orderDetails.stream().anyMatch(orderDetail -> orderDetail.getCode().equals(originalCode));
    }

    private OrderDetail getOriginalOrder(String originalCode, String token) {
        return ordersRepository.getById(originalCode, token);
    }

    private boolean hasValidSessionDates(OrderDetail order, ZonedDateTime sessionGTE, ZonedDateTime sessionLTE) {
        for (OrderDetailItem item : order.getItems()) {
            if (sessionGTE != null && item.getTicket().getAllocation() != null
                && !item.getTicket().getAllocation().getSession().getDate().getStart().isAfter(sessionGTE)) {
                return false;
            }
            if (sessionLTE != null && item.getTicket().getAllocation() != null
                && !item.getTicket().getAllocation().getSession().getDate().getStart().isBefore(sessionLTE)) {
                return false;
            }
        }
        return true;
    }
}
