package es.onebox.mgmt.datasources.ms.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.ms.order.dto.ProductBarcodesResponseDTO;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.dto.SearchOrderRequest;
import es.onebox.mgmt.datasources.ms.order.dto.SearchOrderResponse;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesExportRequest;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExportsErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.enums.ExportType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsOrderDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/orders-api/" + API_VERSION;
    private static final String PRODUCTS = "/products";
    private static final String ORDERS = "/orders";
    private static final String ORDER = ORDERS + "/{orderCode}";
    private static final String UPLOADED_EXTERNAL_BARCODES = "/product-barcodes";
    private static final int TIMEOUT = 60000;

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("EXPORT_LIMIT_REACHED", ApiMgmtExportsErrorCode.EXPORT_LIMIT_REACHED);
        ERROR_CODES.put("PRODUCT_NOT_AVAILABLE", ApiMgmtErrorCode.PRODUCT_NOT_AVAILABLE);
        ERROR_CODES.put("PRODUCT_YET_TO_BE_RELEASED", ApiMgmtErrorCode.PRODUCT_YET_TO_BE_RELEASED);
        ERROR_CODES.put("ALREADY_RELEASED_PRODUCT", ApiMgmtErrorCode.ALREADY_RELEASED_PRODUCT);
        ERROR_CODES.put("INVALID_QUOTA_ID", ApiMgmtErrorCode.INVALID_QUOTA_ID);
    }

    @Autowired
    public MsOrderDatasource(@Value("${clients.services.ms-order}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             TracingInterceptor tracingInterceptor) {
        httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
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

    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request).build();
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS)
                .params(params)
                .execute(ProductSearchResponse.class);
    }

    public OrderDTO getOrder(String orderCode) {
        return httpClient.buildRequest(HttpMethod.GET, ORDER)
                .pathParams(orderCode)
                .execute(OrderDTO.class);
    }

    public ProductBarcodesResponseDTO getExternalBarcodes(Long eventId, Long sessionId, String barcode, Long limit, Long offset) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("eventId", eventId);
        builder.addQueryParameter("sessionId", sessionId);
        builder.addQueryParameter("barcodeOrderProvider", "EXTERNAL");
        builder.addQueryParameter("barcode", barcode);
        builder.addQueryParameter("limit", limit);
        builder.addQueryParameter("offset", offset);
        return httpClient.buildRequest(HttpMethod.GET, UPLOADED_EXTERNAL_BARCODES)
                .params(builder.build())
                .execute(ProductBarcodesResponseDTO.class);
    }

    public ExportProcess exportSeasonTicketReleases(SeasonTicketReleasesExportRequest filter) {
        ClientRequestBody body = new ClientRequestBody(filter);
        return httpClient.buildRequest(HttpMethod.POST, "/season-ticket-releases/report").body(body)
                .execute(ExportProcess.class);
    }

    public ExportProcess getExportStatus(String exportId, Long userId, ExportType type) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("type", type);
        return httpClient.buildRequest(HttpMethod.GET, "/exports/{exportId}/users/{userId}/status")
                .pathParams(exportId, userId)
                .params(builder.build())
                .execute(ExportProcess.class);
    }

}