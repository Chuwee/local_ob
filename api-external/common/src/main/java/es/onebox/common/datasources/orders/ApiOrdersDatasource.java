package es.onebox.common.datasources.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.SearchOrdersResponse;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApiOrdersDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/orders-mgmt-api/" + API_VERSION;

    private static final String ORDERS = "/orders";
    private static final String MEMBER_ORDERS = "/member-orders";
    private static final String CHANNEL_ID = "channel_id";
    private static final String PURCHASE_DATE_FROM = "purchase_date_from";
    private static final String PURCHASE_DATE_TO = "purchase_date_to";
    private static final String OFFSET = "offset";
    private static final String ORDER_CODE = "/{orderCode}";
    private static final String INCLUDE_UPDATED_REFUNDS = "include_updated_refunds";

    private final HttpClient httpClient;
    private static final long CONNECTION_TIMEOUT = 10000L;

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
    public ApiOrdersDatasource(@Value("${clients.services.api-orders-mgmt}") String baseUrl,
                             ObjectMapper jacksonMapper, TracingInterceptor tracingInterceptor
    ) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public OrderDetail getOrder(String orderCode, String token) {
        return httpClient.buildRequest(HttpMethod.GET, ORDERS + ORDER_CODE)
                .pathParams(orderCode)
                .headers(prepareAuthHeader(token))
                .execute(OrderDetail.class);
    }

    public HashMap getRawOrder(String orderCode, String token) {
        return httpClient.buildRequest(HttpMethod.GET, ORDERS + ORDER_CODE)
                .pathParams(orderCode)
                .headers(prepareAuthHeader(token))
                .execute(HashMap.class);
    }

    public HashMap getRawMemberOrder(String orderCode, String token) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBER_ORDERS + ORDER_CODE)
                .pathParams(orderCode)
                .headers(prepareAuthHeader(token))
                .execute(HashMap.class);
    }

    public SearchOrdersResponse getOrders(String token, List<Long> channelIds, ZonedDateTime from, ZonedDateTime to, Long offset, Boolean includeUpdatedRefunds) {
        Map<String, Object> params = new HashMap<>();
        params.put(CHANNEL_ID, channelIds.stream().map(Object::toString)
                .collect(Collectors.joining(",")));
        params.put(PURCHASE_DATE_FROM, from);
        params.put(PURCHASE_DATE_TO, to);
        params.put(OFFSET, offset);
        params.put(INCLUDE_UPDATED_REFUNDS, includeUpdatedRefunds);
        return httpClient.buildRequest(HttpMethod.GET, ORDERS)
                .params(DatasourceUtils.prepareQueryParams(params))
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(SearchOrdersResponse.class);
    }

    private RequestHeaders prepareAuthHeader(String token) {
        return new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
