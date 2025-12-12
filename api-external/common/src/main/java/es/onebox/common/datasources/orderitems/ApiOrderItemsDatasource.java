package es.onebox.common.datasources.orderitems;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.datasources.orderitems.dto.SearchOrderItemsResponse;
import es.onebox.common.datasources.orderitems.dto.request.OrderItemsRequestParameters;
import es.onebox.common.datasources.orderitems.enums.OrderItemState;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


@Component
public class ApiOrderItemsDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/orders-mgmt-api/" + API_VERSION;

    private static final String ORDER_ITEMS = "/order-items";
    private static final String STATE = "state";
    private static final String PURCHASE_DATE_FROM = "purchase_date_from";
    private static final String PURCHASE_DATE_TO = "purchase_date_to";
    private static final String OFFSET = "offset";
    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("AOMG0000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("AOMG0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("AOMG0005", ApiExternalErrorCode.TOO_MUCH_ELEMENTS_TO_PROCESS);
        ERROR_CODES.put("ORDER_NOT_FOUND", ApiExternalErrorCode.ORDER_NOT_FOUND);
        ERROR_CODES.put("ORDER_ITEM_NOT_FOUND", ApiExternalErrorCode.ORDER_ITEM_NOT_FOUND);
        ERROR_CODES.put("ORDER_STATE_INVALID", ApiExternalErrorCode.ORDER_STATE_INVALID);
        ERROR_CODES.put("PRODUCT_STATE_INVALID", ApiExternalErrorCode.PRODUCT_STATE_INVALID);
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("ACCESS_DENIED", ApiExternalErrorCode.ACCESS_DENIED);
    }

    @Autowired
    public ApiOrderItemsDatasource(@Value("${clients.services.api-orders-mgmt}") String baseUrl,
                                   TracingInterceptor tracingInterceptor) {
        ObjectMapper mapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(mapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, mapper))
                .build();
    }

    public SearchOrderItemsResponse getOrderItems(String token, ZonedDateTime from, ZonedDateTime to, Long offset) {
        Map<String, Object> params = new HashMap<>();
        params.put(STATE, OrderItemState.PURCHASE.name());
        params.put(PURCHASE_DATE_FROM, from);
        params.put(PURCHASE_DATE_TO, to);
        params.put(OFFSET, offset);
        return httpClient.buildRequest(HttpMethod.GET, ORDER_ITEMS)
                .params(DatasourceUtils.prepareQueryParams(params))
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(SearchOrderItemsResponse.class);
    }

    public SearchOrderItemsResponse getOrderItems(String accessToken, OrderItemsRequestParameters parameters) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(parameters)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, ORDER_ITEMS)
                .params(query)
                .headers(DatasourceUtils.prepareAuthHeader(accessToken))
                .execute(SearchOrderItemsResponse.class);
    }
}
