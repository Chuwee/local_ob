package es.onebox.event.datasources.ms.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventErrorCode;
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
    private static final String CHANNELS = "/channels";
    private static final String CHANNEL = CHANNELS + "/{channelId}";

    private static final int READ_TIMEOUT = 60000;
    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("CHANNEL_CONFIG_NOT_FOUND", MsEventErrorCode.CHANNEL_NOT_FOUND);
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
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    public ChannelAttributes getChannelAttributes(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL + "/attributes")
                .pathParams(channelId)
                .execute(ChannelAttributes.class);
    }

    public ChannelConfigDTO getChannelConfig(Long channelId) {
            return httpClient.buildRequest(HttpMethod.GET, CHANNEL + "/config")
                .pathParams(channelId)
                .execute(ChannelConfigDTO.class);
    }

}
