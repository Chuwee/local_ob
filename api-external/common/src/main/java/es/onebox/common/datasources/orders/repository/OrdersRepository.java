package es.onebox.common.datasources.orders.repository;


import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orders.ApiOrdersDatasource;
import es.onebox.common.datasources.orders.dto.Order;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.SearchOrdersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Repository
public class OrdersRepository {

    private final ApiOrdersDatasource apiOrdersDatasource;
    private final TokenRepository tokenRepository;

    @Autowired
    public OrdersRepository(ApiOrdersDatasource apiOrdersDatasource, TokenRepository tokenRepository) {
        this.apiOrdersDatasource = apiOrdersDatasource;
        this.tokenRepository = tokenRepository;
    }


    public List<Order> getOrders(String token, List<Long> channelIds, ZonedDateTime from, ZonedDateTime to, Boolean includeUpdatedRefunds) {
        List<Order> orders = new ArrayList<>();
        Long offset = 0L;
        while (offset != null) {
            SearchOrdersResponse result = apiOrdersDatasource.getOrders(token, channelIds, from, to ,offset, includeUpdatedRefunds);
            orders.addAll(result.getData());
            offset = result.getMetadata().nextOffset();
        }

        return orders;
    }

    @Cached(key = "api_orders_mgmt_order_detail", expires = 600)
    public OrderDetail getById(@CachedArg String orderCode, @CachedArg String token) {
        return apiOrdersDatasource.getOrder(orderCode, token);
    }

    @Cached(key = "api_orders_mgmt_order_map_detail", expires = 600)
    public HashMap getRawOrder(@CachedArg String orderCode, @CachedArg String token) {
        return apiOrdersDatasource.getRawOrder(orderCode, token);
    }

    @Cached(key = "api_orders_mgmt_member_order_map_detail", expires = 600)
    public HashMap getRawMemberOrder(@CachedArg String orderCode, @CachedArg String token) {
        return apiOrdersDatasource.getRawMemberOrder(orderCode, token);
    }
}
