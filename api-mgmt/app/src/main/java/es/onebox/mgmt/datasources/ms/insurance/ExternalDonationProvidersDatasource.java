package es.onebox.mgmt.datasources.ms.insurance;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider.AvailableCampaigns;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExternalDonationProvidersDatasource {

    private static final String API_VERSION = "/1.0";
    private static final String BASE_PATH = "/ms-insurance" + API_VERSION;
    private static final String DONATIONS_PROVIDERS = "/donation-providers";
    private static final String CAMPAIGNS = "/campaigns";
    private static final String PROVIDER_ID = "/{providerId}";
    private static final int TIMEOUT = 60000;

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("INVALID_DONATION_API_KEY", ApiMgmtErrorCode.INVALID_DONATION_API_KEY);
        ERROR_CODES.put("DONATION_CONFIG_NOT_ENABLED", ApiMgmtErrorCode.DONATION_CONFIG_NOT_ENABLED);
        ERROR_CODES.put("INVALID_DONATION_PROVIDER_ID", ApiMgmtErrorCode.INVALID_DONATION_PROVIDER_ID);
    }

    public static ErrorCode getErrorCode(String msEventErrorCode) {
        return ERROR_CODES.getOrDefault(msEventErrorCode, ApiMgmtErrorCode.GENERIC_ERROR);
    }

    @Autowired
    public ExternalDonationProvidersDatasource(@Value("${clients.services.ms-insurance}") String baseUrl,
                                               ObjectMapper jacksonMapper,
                                               TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public AvailableCampaigns getDonationCampaigns(Long entityId, Long providerId) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("entityId", entityId).
                build();

        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + DONATIONS_PROVIDERS + PROVIDER_ID + CAMPAIGNS)
                .pathParams(providerId)
                .params(params)
                .execute(AvailableCampaigns.class);
    }

}
