package es.onebox.ms.notification.datasources.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.flc.ie.model.Status;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalDatasource {

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    private final HttpClient httpClient;

    public ExternalDatasource(ObjectMapper jacksonMapper,
                              TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public Status call(String url, Object body, Map<String, String> headers) {
        RequestHeaders.Builder builder = new RequestHeaders.Builder();
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(builder::addHeader);
        }
        return httpClient.buildRequest(HttpMethod.POST, url).body(new ClientRequestBody(body)).headers(builder.build()).execute(Status.class);
    }
}
