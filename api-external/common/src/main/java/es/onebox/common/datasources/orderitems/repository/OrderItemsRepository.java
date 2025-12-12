package es.onebox.common.datasources.orderitems.repository;

import es.onebox.common.datasources.orderitems.ApiOrderItemsDatasource;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.SearchOrderItemsResponse;
import es.onebox.common.datasources.orderitems.dto.request.OrderItemsRequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderItemsRepository {

    private final ApiOrderItemsDatasource apiOrderItemsDatasource;

    @Autowired
    public OrderItemsRepository(ApiOrderItemsDatasource apiOrderItemsDatasource) {
        this.apiOrderItemsDatasource = apiOrderItemsDatasource;
    }

    public List<OrderItem> getOrderItems(String token, ZonedDateTime from, ZonedDateTime to) {
        List<OrderItem> orderItems = new ArrayList<>();
        Long offset = 0L;
        while (offset != null) {
            SearchOrderItemsResponse result = apiOrderItemsDatasource.getOrderItems(token, from, to ,offset);
            orderItems.addAll(result.getData());
            offset = result.getMetadata().nextOffset();
        }

        return orderItems;
    }

    public SearchOrderItemsResponse getOrderItems(String token, OrderItemsRequestParameters parameters) {
        return apiOrderItemsDatasource.getOrderItems(token, parameters);
    }



}


