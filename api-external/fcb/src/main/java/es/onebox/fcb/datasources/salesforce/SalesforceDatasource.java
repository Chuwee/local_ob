package es.onebox.fcb.datasources.salesforce;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.fcb.datasources.config.FcbSalesforceProperties;
import es.onebox.fcb.datasources.salesforce.dto.RequestAbandonedOrderDTO;
import es.onebox.fcb.datasources.salesforce.dto.ResponseAbandonedDataDTO;
import es.onebox.fcb.datasources.salesforce.exception.SalesforceHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SalesforceDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/hub/" + API_VERSION;
    private final HttpClient httpClient;

    private String datasetKey;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    static {
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
    }


    @Autowired
    public SalesforceDatasource(FcbSalesforceProperties fcbSalesforceProperties,
                                ObjectMapper jacksonMapper,
                                TracingInterceptor tracingInterceptorFCB) {
        datasetKey = fcbSalesforceProperties.getDataset();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(fcbSalesforceProperties.getUrl() + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptorFCB)
                .exceptionBuilder(new SalesforceHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public ResponseAbandonedDataDTO storeAbandonedOrder(String transactionId, RequestAbandonedOrderDTO requestAbandonedOrder, String token) {
        RequestHeaders requestHeaders = new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token).build();
        return httpClient.buildRequest(HttpMethod.PUT, "/dataevents/key:{key}/rows/IDTransaction:{transactionId}")
                .pathParams(datasetKey, transactionId)
                .headers(requestHeaders)
                .body(new ClientRequestBody(requestAbandonedOrder))
                .execute(ResponseAbandonedDataDTO.class);
    }

}
