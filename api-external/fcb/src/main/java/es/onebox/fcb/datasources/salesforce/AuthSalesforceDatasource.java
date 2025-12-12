package es.onebox.fcb.datasources.salesforce;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.fcb.datasources.config.FcbSalesforceProperties;
import es.onebox.fcb.datasources.salesforce.dto.RequestTokenDTO;
import es.onebox.fcb.datasources.salesforce.dto.ResponseTokenDTO;
import es.onebox.fcb.datasources.salesforce.exception.SalesforceHttpExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthSalesforceDatasource {

    private static final String API_VERSION = "v2";
    private static final String BASE_PATH = "/" + API_VERSION;

    private static final String GRANT_TYPE = "client_credentials";
    private String id;
    private String secret;

    private final HttpClient httpClient;
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    static {
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
    }

    @Autowired
    public AuthSalesforceDatasource(FcbSalesforceProperties fcbSalesforceProperties,
                                    ObjectMapper jacksonMapper) {
        this.id = fcbSalesforceProperties.getId();
        this.secret = fcbSalesforceProperties.getSecret();

        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(fcbSalesforceProperties.getAuth() + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new SalesforceHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public ResponseTokenDTO getToken() {
        RequestTokenDTO getTokenDTO = new RequestTokenDTO();
        getTokenDTO.setGrantType(GRANT_TYPE);
        getTokenDTO.setClientId(id);
        getTokenDTO.setClientSecret(secret);
        return httpClient.buildRequest(HttpMethod.POST, "/token")
                .body(new ClientRequestBody(getTokenDTO))
                .execute(ResponseTokenDTO.class);
    }

}
