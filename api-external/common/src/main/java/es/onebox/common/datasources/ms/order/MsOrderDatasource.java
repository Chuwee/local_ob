package es.onebox.common.datasources.ms.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.datasources.ms.order.dto.OrderActionResponse;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderGroup;
import es.onebox.common.datasources.ms.order.dto.OrderProductRequest;
import es.onebox.common.datasources.ms.order.dto.OrderSearchResponse;
import es.onebox.common.datasources.ms.order.dto.PreOrderDTO;
import es.onebox.common.datasources.ms.order.dto.VisitorGroupParam;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceDTO;
import es.onebox.common.datasources.ms.order.dto.response.barcodes.ProductBarcodesResponse;
import es.onebox.common.datasources.ms.order.request.InvoiceSearchParam;
import es.onebox.common.datasources.ms.order.request.barcodes.ProductBarcodesSearchRequest;
import es.onebox.common.datasources.orders.dto.OrderSearchRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsOrderDatasource {
    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/orders-api/" + API_VERSION;

    private static final String ORDERS = "/orders";
    private static final String PREORDERS = "/preorders";

    private final HttpClient httpClient;
    private static final long CONNECTION_TIMEOUT = 10000L;
    private static final long READ_TIMEOUT = 10000L;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("500MO000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("404MO000", ApiExternalErrorCode.NOT_FOUND);
    }

    @Autowired
    public MsOrderDatasource(@Value("${clients.services.ms-order}") String baseUrl,
                             ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public OrderActionResponse getOrderAction(String orderCode) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + ORDERS + "/{orderCode}/actions")
                .pathParams(orderCode)
                .execute(OrderActionResponse.class);
    }

    public void upsertOrderAction(String orderCode, OrderProductRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + ORDERS + "/{orderCode}/actions")
                .pathParams(orderCode)
                .body(new ClientRequestBody(request, ClientRequestBody.Type.JSON))
                .execute();
    }

    public OrderDTO getOrderInfo(String code, Integer entityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entityId", entityId);

        return httpClient.buildRequest(HttpMethod.GET, ORDERS + "/info/{code}")
                .pathParams(code)
                .params(DatasourceUtils.prepareQueryParams(params))
                .execute(OrderDTO.class);
    }

    public PreOrderDTO getPreOrderInfo(String code, Integer entityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entityId", entityId);

        return httpClient.buildRequest(HttpMethod.GET, PREORDERS + "/code/{code}")
                .pathParams(code)
                .params(DatasourceUtils.prepareQueryParams(params))
                .execute(PreOrderDTO.class);
    }

    public PreOrderDTO getPreOrderInfo(String token) {
        return httpClient.buildRequest(HttpMethod.GET, PREORDERS + "/{token}")
                .pathParams(token)
                .execute(PreOrderDTO.class);
    }

    public List<OrderGroup> searchGroups(VisitorGroupParam param) {
        return httpClient.buildRequest(HttpMethod.POST, "/orders/groups/search")
                .body(new ClientRequestBody(param))
                .execute(ListType.of(OrderGroup.class));
    }

    public OrderGroup getGroup(Long groupId) {
        return httpClient.buildRequest(HttpMethod.GET, "/orders/groups/{groupId}")
                .pathParams(groupId)
                .execute(OrderGroup.class);
    }

    public OrderDTO getOrderByCode(String code) {
        return httpClient.buildRequest(HttpMethod.GET, "/orders/{code}")
                .pathParams(code)
                .execute(OrderDTO.class);
    }

    public List<InvoiceDTO> searchInvoices(InvoiceSearchParam param) {
        return httpClient.buildRequest(HttpMethod.POST, "/orders/invoices/search")
                .body(new ClientRequestBody(param))
                .execute(ListType.of(InvoiceDTO.class));
    }

    public OrderSearchResponse searchOrders(OrderSearchRequest request) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + "/orders")
                .params(query)
                .execute(OrderSearchResponse.class);
    }

    public ProductBarcodesResponse getProductBarcodes(ProductBarcodesSearchRequest request) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + "/product-barcodes")
                .params(query)
                .execute(ProductBarcodesResponse.class);
    }
}
