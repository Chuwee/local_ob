package es.onebox.atm.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.atm.users.dto.ATMChangeromotionStatusRequest;
import es.onebox.atm.users.dto.ATMOauthResponse;
import es.onebox.atm.users.dto.ATMUserPromotion;
import es.onebox.atm.users.dto.ChangePromotionStatusResponse;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ATMPromotionsDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/catalog-api/" + API_VERSION;

    private static final String LOGIN = "/events";

    private static final long CONNECTION_TIMEOUT = 10000L;

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {

    }

    @Autowired
    public ATMPromotionsDatasource() {
        ObjectMapper mapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .jacksonMapper(mapper)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, mapper))
                .build();
    }

    public ATMOauthResponse login(String loginUrl, String clientId, String secret) {
        RequestHeaders requestHeaders = new RequestHeaders.Builder()
                .addHeader("client_id", clientId)
                .addHeader("client_secret", secret)
                .addHeader("grant_type", "CLIENT_CREDENTIALS").build();
        return httpClient.buildRequest(HttpMethod.POST, loginUrl )
                .headers(requestHeaders)
                .execute(ATMOauthResponse.class);
    }

    public List<ATMUserPromotion> getUserPromotions(String promotionsUrl, String userSalesforceId, String token, String pathParams) {
        RequestHeaders requestHeaders = new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token).build();
        return httpClient
                .buildRequest(HttpMethod.GET, promotionsUrl + "/" + userSalesforceId + pathParams)
                .headers(requestHeaders)
                .execute(ListType.of(ATMUserPromotion.class));
    }

    public ChangePromotionStatusResponse changeUserPromotionStatus(String promotionsUrl, String userSalesforceId, String salesforcePromotionCode, String status, String token) {
        ATMChangeromotionStatusRequest atmChangeromotionStatusRequest = new ATMChangeromotionStatusRequest(salesforcePromotionCode, status);
        RequestHeaders requestHeaders = new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token).build();
        return httpClient.buildRequest(HttpMethod.PUT, promotionsUrl + "/" + userSalesforceId)
                .body(new ClientRequestBody(atmChangeromotionStatusRequest))
                .headers(requestHeaders)
                .execute(ChangePromotionStatusResponse.class);
    }

}
