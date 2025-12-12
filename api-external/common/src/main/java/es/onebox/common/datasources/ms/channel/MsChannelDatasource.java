package es.onebox.common.datasources.ms.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodsDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelEventDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.common.datasources.ms.channel.dto.EmailServerDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsFilter;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelEventSaleRestrictionResponse;
import es.onebox.common.datasources.ms.channel.filter.ChannelConfigsFilter;
import es.onebox.common.datasources.ms.channel.filter.ChannelsFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class MsChannelDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-channel-api/" + API_VERSION;

    private final HttpClient httpClient;
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    static {
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("CHANNEL_FORM_NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("INVALID_CHANNEL_TYPE_EVENT_SALE_RESTRICTIONS", ApiExternalErrorCode.INVALID_CHANNEL_TYPE_EVENT_SALE_RESTRICTIONS);
        ERROR_CODES.put("INVALID_PARAM", ApiExternalErrorCode.INVALID_PARAM);
    }


    @Autowired
    public MsChannelDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
                               ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public ChannelConfigDTO getChannelConfig(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/config")
                .pathParams(channelId)
                .execute(ChannelConfigDTO.class);
    }

    public ChannelConfigResponse getChannelConfigs(ChannelConfigsFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, "/channels/configs")
                .params(params)
                .execute(ChannelConfigResponse.class);
    }

    public ChannelConfigDTO getChannelConfigByPath(String channelPath) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/config/{channelPath}")
                .pathParams(channelPath)
                .execute(ChannelConfigDTO.class);
    }

    public void putChannelConfig(Long channelId, ChannelConfigDTO channelConfig) {
        httpClient.buildRequest(HttpMethod.PUT, "/channels/{channelId}/config")
                .pathParams(channelId)
                .body(new ClientRequestBody(channelConfig))
                .execute();
    }

    public ChannelsResponse getChannels(ChannelsFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, "/channels")
                .params(params)
                .execute(ChannelsResponse.class);
    }

    public ChannelDTO getChannel(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}").pathParams(channelId)
                .execute(ChannelDTO.class);
    }

    public ChannelDeliveryMethodsDTO getChannelDeliveryMethods(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/delivery-methods")
                .pathParams(channelId)
                .execute(ChannelDeliveryMethodsDTO.class);
    }

    public EmailServerDTO getChannelEmailServerConfiguration(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/notifications/email/server")
                .pathParams(channelId)
                .execute(EmailServerDTO.class);
    }

    public MsSaleRequestsResponseDTO searchSaleRequests(Long channelId, Long eventId) {
        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(eventId));
        filter.setLimit(10L);
        filter.setOffset(0L);
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();

        return httpClient.buildRequest(HttpMethod.GET, "/sale-requests")
                .params(params)
                .execute(MsSaleRequestsResponseDTO.class);
    }

    public MsSaleRequestsResponseDTO searchSaleRequests(MsSaleRequestsFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, "/sale-requests")
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(MsSaleRequestsResponseDTO.class);
    }

    public ChannelEventDTO getChannelEventRelationship(Long channelId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/events/{eventId}")
                .pathParams(channelId, eventId)
                .execute(ChannelEventDTO.class);
    }

    public ChannelFormsResponse getChannelFormByType(Long channelId, String formType) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/forms/{formType}")
                .pathParams(channelId, formType)
                .execute(ChannelFormsResponse.class);
    }

    public ChannelEventSaleRestrictionResponse getEventSaleRestrictions (Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/channels/{channelId}/event-sale-restrictions")
                .pathParams(channelId)
                .execute(ChannelEventSaleRestrictionResponse.class);
    }
}
