package es.onebox.ms.notification.datasources.ms.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.ms.notification.datasources.ms.channel.dto.Channel;
import es.onebox.datasource.http.method.HttpMethod;

import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolsDTO;
import es.onebox.ms.notification.exception.ClientHttpExceptionBuilder;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import okhttp3.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsChannelDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-channel-api/" + API_VERSION;
    private static final String CHANNEL = "/channels/{channelId}";
    private static final String EXTERNAL_TOOLS = "/channels/{channelId}/external-tools";
    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("CHANNEL_NOT_FOUND", MsNotificationErrorCode.CHANNEL_NOT_FOUND);
    }

    private final HttpClient httpClient;


    @Autowired
    public MsChannelDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
                               ObjectMapper jacksonMapper,
                               Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .connectTimeout(2000)
                .readTimeout(10000)
                .build();
    }

    public Channel getChannel(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL)
                .pathParams(id)
                .execute(Channel.class);
    }

    public ChannelExternalToolsDTO getChannelExternalTools(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_TOOLS)
                .pathParams(id)
                .execute(ChannelExternalToolsDTO.class);
    }
}
