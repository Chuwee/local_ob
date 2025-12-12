package es.onebox.common.datasources.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.dispatcher.dto.CheckStatusResponse;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoConnectorRequest;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoResponse;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import okhttp3.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntDispatcherDatasource {

    private final HttpClient httpClient;
    private static final String API_VERSION = "1.0";
    public static final String BASE_PATH = "/partners-api/" + API_VERSION;

    @Value("${clients.services.int-dispatcher-service}")
    private String intDispatcherURL;
    @Value("${clients.services.int-avet-connector}")
    private String intAvetConnectorURL;

    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_HEADER_VALUE = "application/json";
    private static final String CONTENTTYPE_HEADER = "Content-Type";
    private static final String CONTENTTYPE_VALUE = "application/json";

    public static final String QUERY_PARAM_PARTNER_PASS_CAPS = "partnerPass";
    public static final String QUERY_PARAM_VENUE_ID = "venueId";
    public static final String QUERY_PARAM_CAPACITY_ID = "capacityId";
    public static final String QUERY_PARAM_PARTNER_ID = "partnerId";

    public static final String GET_PARTNER_INFO = "/partner-info";

    private static final int TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 5000;

    @Autowired
    public IntDispatcherDatasource(ObjectMapper jacksonMapper, Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(TIMEOUT)
                .build();
    }

    public CheckStatusResponse checkStatus(String clubCode) {
        String localBaseUrl = intAvetConnectorURL.replaceFirst("connector", "connector-" + clubCode);
        return httpClient.buildRequest(HttpMethod.GET,
                        localBaseUrl + "/int-avet-connector-api/v1/connection/status")
                .execute(CheckStatusResponse.class);
    }

    public PartnerInfoResponse getPartnerInformation(PartnerInfoConnectorRequest request) {
        RequestHeaders.Builder headers = new RequestHeaders.Builder();
        headers.addHeader(ACCEPT_HEADER, ACCEPT_HEADER_VALUE);
        headers.addHeader(CONTENTTYPE_HEADER, CONTENTTYPE_VALUE);
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_PARTNER_PASS_CAPS, request.getPartnerPass())
                .addQueryParameter(QUERY_PARAM_PARTNER_ID, request.getMemberId())
                .addQueryParameter(QUERY_PARAM_VENUE_ID, request.getEntityId())
                .addQueryParameter(QUERY_PARAM_CAPACITY_ID, request.getCapacityId())
                .build();
        return httpClient.buildRequest(HttpMethod.GET, intDispatcherURL + BASE_PATH + GET_PARTNER_INFO)
                .headers(headers.build())
                .params(params)
                .execute(PartnerInfoResponse.class);
    }

}
