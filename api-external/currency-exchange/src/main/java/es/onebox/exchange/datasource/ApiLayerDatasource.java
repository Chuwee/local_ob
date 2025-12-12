package es.onebox.exchange.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.exchange.config.ApiLayerProperties;
import es.onebox.exchange.dto.ExchangeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiLayerDatasource {

    private static final String LIVE = "/live";
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    private final HttpClient httpClient;
    private final ApiLayerProperties apiLayerProperties;


    @Autowired
    public ApiLayerDatasource(ApiLayerProperties apiLayerProperties,
                              ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(apiLayerProperties.getUrl())
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
        this.apiLayerProperties = apiLayerProperties;
    }

    public ExchangeResponse getCurrenciesExchange(String currencyCode) {
        return httpClient.buildRequest(HttpMethod.GET, LIVE)
                .headers(getHeaders(apiLayerProperties.getApiKey()))
                .params(new QueryParameters.Builder() .addQueryParameter("source", currencyCode).build())
                .execute(ExchangeResponse.class);
    }

    private RequestHeaders getHeaders(String token) {
        return new RequestHeaders.Builder()
                .addHeader("apikey", token)
                .build();
    }

}
