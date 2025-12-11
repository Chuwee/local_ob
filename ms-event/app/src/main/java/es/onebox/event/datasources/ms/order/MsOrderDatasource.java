package es.onebox.event.datasources.ms.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.enums.ProductType;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.event.datasources.ms.order.dto.NumberOperationsRequest;
import es.onebox.event.datasources.ms.order.dto.ProductActiveProductsRequest;
import es.onebox.event.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.dto.ProductSortableField;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsRequest;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsResponse;
import es.onebox.event.datasources.ms.order.dto.SearchOrderRequest;
import es.onebox.event.datasources.ms.order.dto.SearchOrderResponse;
import okhttp3.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MsOrderDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/orders-api/" + API_VERSION;

    private final HttpClient httpClient;
    private final HttpClient httpClientBase;

    @Autowired
    public MsOrderDatasource(@Value("${clients.services.ms-order}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
        this.httpClientBase = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
    }

    public SearchOrderResponse searchOrders(SearchOrderRequest request) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, "/orders")
                .params(query)
                .execute(SearchOrderResponse.class);
    }

    public SearchOperationsResponse searchOperations(SearchOperationsRequest request) {
        return httpClientBase.buildRequest(HttpMethod.POST, "/orders/search")
                .body(new ClientRequestBody(request))
                .execute(SearchOperationsResponse.class);
    }

    public Map<Long, Long> sessionOperations(List<Integer> sessionIds) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameter("sessionIds", sessionIds)
                .build();
        // this hashmap contains numeric String keys instead of Long keys
        HashMap<String, Long> response = httpClientBase.buildRequest(HttpMethod.GET, "/orders/sessionOperations")
                .params(query)
                .execute(HashMap.class);
        return response.entrySet().stream().collect(Collectors.toMap(entry -> Long.valueOf(entry.getKey()), Map.Entry::getValue));
    }

    public Integer numberOperations(List<Integer> sessionIds, List<OrderState> orderStates, List<Integer> rateIds) {
        NumberOperationsRequest numberOperationsRequest = new NumberOperationsRequest();
        numberOperationsRequest.setSessionIds(sessionIds);
        numberOperationsRequest.setOrderStates(orderStates);
        numberOperationsRequest.setRateIds(rateIds);
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(numberOperationsRequest)
                .build();
        return httpClientBase.buildRequest(HttpMethod.GET, "/orders/numberOperations")
                .params(query)
                .execute(Integer.class);
    }

    public ProductSearchResponse getAlmostOneActiveProduct(List<Long> eventIds) {
        return getActiveProducts(eventIds, null, 0L, 1L, null, null);
    }

    public ProductSearchResponse getActiveUserProducts(List<Long> eventIds, List<Long> channelEntityIds, Long offset, Long limit) {
        return getActiveProducts(eventIds, null, offset, limit, Boolean.TRUE, channelEntityIds);
    }

    public ProductSearchResponse getPurchasedProducts(List<Long> variantIds, Long offset, Long limit) {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setVariantIds(variantIds);
        request.setProductTypes(List.of(ProductType.PRODUCT));
        request.setOrderStates(List.of(OrderState.PAID));
        request.setOrderTypes(List.of(OrderType.PURCHASE));
        request.setOffset(offset);
        request.setLimit(limit);
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return this.httpClient.buildRequest(HttpMethod.GET, "/products")
                .params(query)
                .execute(ProductSearchResponse.class);
    }

    public ProductSearchResponse getActiveProducts(List<Long> eventIds, SortOperator<ProductSortableField> sort,
                                                   Long offset, Long limit, Boolean userProducts, List<Long> channelEntityIds) {
        ProductActiveProductsRequest request = new ProductActiveProductsRequest();
        request.setEventIds(eventIds);
        request.setChannelEntityIds(channelEntityIds);
        request.setUserProducts(userProducts);
        request.setSort(sort);
        request.setOffset(offset);
        request.setLimit(limit);
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return this.httpClient.buildRequest(HttpMethod.GET, "/products/active-products")
                .params(query)
                .execute(ProductSearchResponse.class);
    }
}
