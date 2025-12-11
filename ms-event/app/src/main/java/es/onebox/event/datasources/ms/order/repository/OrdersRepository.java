package es.onebox.event.datasources.ms.order.repository;

import com.google.common.collect.Lists;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.event.datasources.ms.order.MsOrderDatasource;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsRequest;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsResponse;
import es.onebox.event.datasources.ms.order.dto.SearchOrderRequest;
import es.onebox.event.datasources.ms.order.dto.SearchOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrdersRepository {

    private static final int MAX_URI_PARAMS_LENGTH = 6000;

    private final MsOrderDatasource msOrderDatasource;

    @Autowired
    public OrdersRepository(MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    public SearchOperationsResponse searchOperations(SearchOperationsRequest request) {
        return msOrderDatasource.searchOperations(request);
    }

    public Long countByEventAndChannel(Long eventId, Long channelId) {
        SearchOrderRequest request = new SearchOrderRequest();
        request.setEventId(eventId);
        request.setChannelId(channelId);
        request.setLimit(0L);
        SearchOrderResponse response = msOrderDatasource.searchOrders(request);
        return response.getMetadata().getTotal();
    }

    public Long countBySession(Long sessionId) {
        SearchOrderRequest request = new SearchOrderRequest();
        request.setSessionId(sessionId);
        request.setLimit(0L);
        SearchOrderResponse response = msOrderDatasource.searchOrders(request);
        return response.getMetadata().getTotal();
    }

    /**
     * Performs call to {@link MsOrderDatasource#sessionOperations(List)}. The call might be splitted into several different
     * calls in order to avoid an HTTP request error due to a URI too large.
     * @param sessionIds
     * @return all combined results
     */
    public Map<Long, Long> sessionOperations(List<Integer> sessionIds) {
        // calculate a pessimistic token length based on the highest sessionId value
        int tokenLength = (int) Math.log10(sessionIds
                .stream()
                .mapToInt(v -> v)
                .max().getAsInt());
        // tokenLength needs to be increased by 1 due to the comma delimiter
        int batchSize = MAX_URI_PARAMS_LENGTH / (tokenLength + 1);

        Map<Long, Long> result = new HashMap<>();
        for (List<Integer> sessionIdsBatch : Lists.partition(sessionIds, batchSize)) {
            Map<Long, Long> response = msOrderDatasource.sessionOperations(sessionIdsBatch);
            if (response != null) {
                result.putAll(response);
            }
        }
        return result;
    }

    public Integer numberOperations(List<Integer> sessionIds, List<OrderState> orderStates, List<Integer> rateIds) {
        return msOrderDatasource.numberOperations(sessionIds, orderStates, rateIds);
    }

    public ProductSearchResponse getAlmostOneActiveProduct(List<Long> eventIds) {
        return msOrderDatasource.getAlmostOneActiveProduct(eventIds);
    }

    public ProductSearchResponse getPurchasedProducts(List<Long> variantIds) {
        return msOrderDatasource.getPurchasedProducts(variantIds, 0L, 200L);
    }

    public ProductSearchResponse getActiveUserProducts(List<Long> eventIds, List<Long> channelEntityIds, Long offset, Long limit) {
        return msOrderDatasource.getActiveUserProducts(eventIds, channelEntityIds, offset, limit);
    }
}
