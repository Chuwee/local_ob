package es.onebox.mgmt.datasources.ms.channel.packsalerequests;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request.FilterPackSalesRequests;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request.UpdatePackSaleRequestStatusDTO;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackChannelSaleRequestStatus;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSaleRequestResponse;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSalesRequestBase;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PackSaleRequestsDatasource {

    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-channel-api/" + API_VERSION + "/sale-requests-packs";

    private static final String SALE_REQUEST_ID = "/{saleRequestId}";
    private static final String SALE_REQUEST_ID_STATUS = SALE_REQUEST_ID + "/status";


    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("FORBIDDEN", ApiMgmtErrorCode.FORBIDDEN);
        ERROR_CODES.put("BAD_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("PACK_SALE_REQUEST_NOT_FOUND", ApiMgmtErrorCode.PACK_SALE_REQUEST_NOT_FOUND);
        ERROR_CODES.put("PACK_SALE_REQUEST_ALREADY_EXISTS", ApiMgmtErrorCode.PACK_SALE_REQUEST_ALREADY_EXISTS);
        ERROR_CODES.put("CHANNEL_PACK_RELATIONSHIP_INCONSISTENT", ApiMgmtErrorCode.CHANNEL_PACK_RELATIONSHIP_INCONSISTENT);
    }

    @Autowired
    public PackSaleRequestsDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
                                      ObjectMapper jacksonMapper,
                                      TracingInterceptor tracingInterceptor) {

        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public PackSaleRequestResponse search(FilterPackSalesRequests filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();

        return httpClient.buildRequest(HttpMethod.GET, StringUtils.EMPTY)
                .params(params)
                .execute(PackSaleRequestResponse.class);
    }

    public PackSalesRequestBase getDetail(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_ID)
                .pathParams(saleRequestId)
                .execute(PackSalesRequestBase.class);
    }

    public void updateStatus(Long saleRequestId, PackSaleRequestStatus status) {
        UpdatePackSaleRequestStatusDTO request = new UpdatePackSaleRequestStatusDTO();
        request.setStatus(PackChannelSaleRequestStatus.getById(status.getId()));
        httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_ID_STATUS)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(request))
                .execute();
    }


}
