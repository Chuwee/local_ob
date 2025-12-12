package es.onebox.ath.orders.service;

import es.onebox.ath.orders.dto.OrderActivatorDTO;
import es.onebox.common.datasources.ms.order.dto.OrderCollectiveDTO;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderSearchResponse;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.orders.dto.OrderSearchRequest;
import es.onebox.core.utils.common.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service(value = "athOrderService")
public class OrderService {

    private static final Long PAGE_ZERO = 0L;
    private static final Long LIMIT = 1000L;

    private final MsOrderRepository msOrderRepository;

    @Autowired
    public OrderService(MsOrderRepository msOrderRepository) {
        this.msOrderRepository = msOrderRepository;
    }


    public List<OrderActivatorDTO> getOrdersActivators(ZonedDateTime purchaseDateFrom, ZonedDateTime purchaseDateTo, Long channelId) {
        List<OrderActivatorDTO> response = new ArrayList<>();

        OrderSearchRequest orderSearchRequest = new OrderSearchRequest();
        orderSearchRequest.setPurchaseDateFrom(purchaseDateFrom);
        orderSearchRequest.setPurchaseDateTo(purchaseDateTo);
        orderSearchRequest.setChannelIds(Arrays.asList(channelId));
        orderSearchRequest.setLimit(LIMIT);
        orderSearchRequest.setOffset(PAGE_ZERO);
        OrderSearchResponse orderSearchResponse = msOrderRepository.searchOrders(orderSearchRequest);

        if (isNull(orderSearchResponse) || CommonUtils.isEmpty(orderSearchResponse.getData())) {
            return null;
        }

        List<OrderDTO> orders = new ArrayList<>(orderSearchResponse.getData());
        Long offset = orderSearchResponse.getMetadata().getOffset() + LIMIT;

        while (offset <= orderSearchResponse.getMetadata().getTotal()) {
            orderSearchRequest.setOffset(offset);
            orderSearchResponse = msOrderRepository.searchOrders(orderSearchRequest);
            offset = offset + LIMIT;
            if (nonNull(orderSearchResponse) && nonNull(orderSearchResponse.getData())) {
                orders.addAll(orderSearchResponse.getData());
            }
        }

        return convertFromOrderDTO(orders);
    }

    private List<OrderActivatorDTO> convertFromOrderDTO(List<OrderDTO> orders) {
        List<OrderActivatorDTO> response = new ArrayList<>();

        for (OrderDTO order : orders) {
            OrderActivatorDTO orderActivatorResponse = new OrderActivatorDTO();
            orderActivatorResponse.setCode(order.getCode());
            orderActivatorResponse.setUser(order.getCollectives().stream().findFirst().map(OrderCollectiveDTO::getUser).orElse(null));
            response.add(orderActivatorResponse);
        }

        return response;
    }
}
