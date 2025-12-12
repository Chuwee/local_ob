package es.onebox.internal.sgtm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProviderPlanSettingsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderPlanSettingsService.class);
    private static final String FEVER_PROVIDER_PLAN_SETTINGS_PATH = "/fever-api/v1/events/{eventId}/channels/{channelId}/provider-plan-settings";
    
    private final HttpClient httpClient;

    @Autowired
    public ProviderPlanSettingsService(@Value("${fever.base.url}") String feverBaseUrl,
                                       ObjectMapper jacksonMapper,
                                       TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(feverBaseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
    }
    
    public void sendProviderPlanSettingsToFever(Long eventId, Long channelId, String providerPlanSettings) {
        LOGGER.info("Sending provider plan settings to Fever for event: {}, channel: {}", eventId, channelId);
        
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("provider_plan_settings", providerPlanSettings);
            
            httpClient.buildRequest(HttpMethod.PUT, FEVER_PROVIDER_PLAN_SETTINGS_PATH)
                    .pathParams(eventId, channelId)
                    .body(new ClientRequestBody(requestBody))
                    .execute();
            
            LOGGER.info("Successfully sent provider plan settings to Fever for event: {}, channel: {}", eventId, channelId);
        } catch (Exception e) {
            LOGGER.error("Error sending provider plan settings to Fever for event: {}, channel: {}", 
                        eventId, channelId, e);
            throw e;
        }
    }
}
