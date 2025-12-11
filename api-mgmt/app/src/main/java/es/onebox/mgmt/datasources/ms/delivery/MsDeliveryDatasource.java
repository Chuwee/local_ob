package es.onebox.mgmt.datasources.ms.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.ms.delivery.dto.ChannelEmailTest;
import es.onebox.mgmt.exception.ApiMgmtDeliveryErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsDeliveryDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/delivery-api/" + API_VERSION;

    private static final String EMAIL = "/mail-services";
    private static final String EMAIL_TEST = EMAIL + "/test";


    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("NOT_FOUND", ApiMgmtErrorCode.NOT_FOUND);
        ERROR_CODES.put("CHANNEL_NOT_FOUND", ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        ERROR_CODES.put("CHANNEL_USES_ONEBOX_SERVER", ApiMgmtDeliveryErrorCode.CHANNEL_USES_ONEBOX_SERVER);
        ERROR_CODES.put("SMTP_CONFIGURATION_ERROR", ApiMgmtDeliveryErrorCode.SMTP_CONFIGURATION_ERROR);
        ERROR_CODES.put("EMAIL_IS_NOT_ALLOWED", ApiMgmtDeliveryErrorCode.EMAIL_IS_NOT_ALLOWED);
        ERROR_CODES.put("EMAIL_PREPARATION_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_PREPARATION_ERROR);
        ERROR_CODES.put("EMAIL_PARSE_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_PARSE_ERROR);
        ERROR_CODES.put("EMAIL_AUTH_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_AUTH_ERROR);
        ERROR_CODES.put("EMAIL_SEND_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_SEND_ERROR);
        ERROR_CODES.put("EMAIL_CONFIG_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_CONFIG_ERROR);
        ERROR_CODES.put("EMAIL_GENERIC_ERROR", ApiMgmtDeliveryErrorCode.EMAIL_GENERIC_ERROR);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsDeliveryDatasource(@Value("${clients.services.ms-delivery}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public void sendTestEmail(ChannelEmailTest body) {
        httpClient.buildRequest(HttpMethod.POST, EMAIL_TEST)
                .body(new ClientRequestBody(body))
                .execute();
    }
}
