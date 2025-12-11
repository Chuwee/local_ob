package es.onebox.mgmt.datasources.ms.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilter;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilterParam;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.GatewayBenefitConfiguration;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiPaymentDatasource {

    private static final String CHANNEL_GATEWAY_CONFIGS = "/payment/channels/{channelId}/gateways";
    private static final String CHANNEL_GATEWAY_CONFIG_DETAIL = CHANNEL_GATEWAY_CONFIGS + "/{gatewaySid}/configs/{configSid}";
    private static final String ALL_GATEWAYS = "/payment/gateways/";
    private static final String GATEWAY_CONFIG = "/payment/gateways/{gateway}";
    private static final String GATEWAY_CONFIG_FIELDS = GATEWAY_CONFIG + "/fields";
    private static final String CHANNEL_GATEWAY_CONFIG = "/payment/channels/{channel}/gateways/{gateway}/configs/{confSid}";
    private static final String GATEWAY_CONFIG_FILTER = "/payment/channels/{channel}/filters/{id}";
    private static final String BENEFITS_CONFIG = "/payment/benefits/gateways";
    private static final String BENEFITS_CONFIG_DETAIL = BENEFITS_CONFIG + "/{gatewaySid}/config/{confSid}/channels/{channelId}/events/{eventId}";

    private static final Map<String, ErrorCode> ERROR_CODES;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("400G0001", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("CANNOT_DISABLE_PAYMENT_METHOD_USED_FOR_BOOKING_CHECKOUT", ApiMgmtErrorCode.CANNOT_DISABLE_PAYMENT_METHOD_USED_FOR_BOOKING_CHECKOUT);
        ERROR_CODES.put("404G0001", ApiMgmtErrorCode.CONFIGURATION_NOT_FOUND);
		ERROR_CODES.put("GATEWAYS_NOT_ALLOWED", ApiMgmtErrorCode.GATEWAYS_NOT_ALLOWED);
    }

    private final HttpClient httpClient;

    @Autowired
    public ApiPaymentDatasource(@Value("${clients.services.api-payment}") String baseUrl,
                                ObjectMapper jacksonMapper,
                                TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public List<ChannelGatewayConfig> getChannelGatewayConfigs(Long channelId) {
        return this.httpClient.buildRequest(HttpMethod.GET, CHANNEL_GATEWAY_CONFIGS)
                .pathParams(channelId)
                .execute(ListType.of(ChannelGatewayConfig.class));
    }

    public List<GatewayConfig> getGateways() {
        return this.httpClient.buildRequest(HttpMethod.GET, ALL_GATEWAYS)
                .execute(ListType.of(GatewayConfig.class));
    }


    public ChannelGatewayConfig getChannelGatewayConfig(Long channelId, String gatewaySid, String configSid) {
        return this.httpClient.buildRequest(HttpMethod.GET, CHANNEL_GATEWAY_CONFIG_DETAIL)
                .pathParams(channelId, gatewaySid, configSid)
                .execute(ChannelGatewayConfig.class);
    }

    public GatewayConfig getGatewayConfig(String gatewaySid) {
        return this.httpClient.buildRequest(HttpMethod.GET, GATEWAY_CONFIG)
                .pathParams(gatewaySid)
                .execute(GatewayConfig.class);
    }

    public void createOrUpdateChannelGatewayConfig(ChannelGatewayConfig config) {
        this.httpClient.buildRequest(HttpMethod.PUT, CHANNEL_GATEWAY_CONFIG)
                .pathParams(config.getChannelId(), config.getGatewaySid(), config.getConfSid())
                .body(new ClientRequestBody(config))
                .execute();
    }

    public void createOrUpdateChannelGatewayConfigs(Long channelId, List<ChannelGatewayConfig> configList) {
        this.httpClient.buildRequest(HttpMethod.PUT, CHANNEL_GATEWAY_CONFIGS)
                .pathParams(channelId)
                .body(new ClientRequestBody(configList))
                .execute();
    }

    public List<String> getGatewayConfigFields(String gatewaySid) {
        return this.httpClient.buildRequest(HttpMethod.GET, GATEWAY_CONFIG_FIELDS)
                .pathParams(gatewaySid)
                .execute(ListType.of(String.class));
    }

    public void deleteChannelGatewayConfig(Long channelId, String gatewaySid, String configSid) {
        this.httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_GATEWAY_CONFIG)
                .pathParams(channelId, gatewaySid, configSid)
                .execute();
    }

    public ChannelGatewayConfigFilter getChannelGatewayConfigFilter(Long channelId, String id) {
        return this.httpClient.buildRequest(HttpMethod.GET, GATEWAY_CONFIG_FILTER)
                .pathParams(channelId, id)
                .execute(ChannelGatewayConfigFilter.class);
    }

    public void updateChannelGatewayConfigFilter(Long channelId, String id, ChannelGatewayConfigFilterParam channelGatewayConfigFilterParam) {
        this.httpClient.buildRequest(HttpMethod.PUT, GATEWAY_CONFIG_FILTER)
                .pathParams(channelId, id)
                .body(new ClientRequestBody(channelGatewayConfigFilterParam))
                .execute();
    }

    public void deleteChannelGatewayConfigFilter(Long channelId, String id) {
        this.httpClient.buildRequest(HttpMethod.DELETE, GATEWAY_CONFIG_FILTER)
                .pathParams(channelId, id)
                .execute();
    }

    public GatewayBenefitConfiguration createGatewayBenefitConfiguration(GatewayBenefitConfiguration configuration) {
        return this.httpClient.buildRequest(HttpMethod.POST, BENEFITS_CONFIG)
                .body(new ClientRequestBody(configuration))
                .execute(GatewayBenefitConfiguration.class);
    }

    public List<GatewayBenefitConfiguration> getListGatewayBenefitConfigurations(Long channelId, Long eventId) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("channelId", channelId).
                addQueryParameter("eventId", eventId).
                build();
        return this.httpClient.buildRequest(HttpMethod.GET, BENEFITS_CONFIG)
                .params(params)
                .execute(ListType.of(GatewayBenefitConfiguration.class));
    }

    public GatewayBenefitConfiguration getGatewayBenefitConfiguration(String gatewaySid, String confSid, Long channelId, Long eventId) {
        return this.httpClient.buildRequest(HttpMethod.GET, BENEFITS_CONFIG_DETAIL)
                .pathParams(gatewaySid, confSid, channelId, eventId)
                .execute(GatewayBenefitConfiguration.class);
    }

    public GatewayBenefitConfiguration updateGatewayBenefitConfiguration(String gatewaySid, String confSid, Long channelId, Long eventId,
                                                                         GatewayBenefitConfiguration patchData) {
        return this.httpClient.buildRequest(HttpMethod.PATCH, BENEFITS_CONFIG_DETAIL)
                .pathParams(gatewaySid, confSid, channelId, eventId)
                .body(new ClientRequestBody(patchData))
                .execute(GatewayBenefitConfiguration.class);
    }

    public void deleteGatewayBenefitConfiguration(String gatewaySid, String confSid,Long channelId,  Long eventId) {
        this.httpClient.buildRequest(HttpMethod.DELETE, BENEFITS_CONFIG_DETAIL)
                .pathParams(gatewaySid, confSid, channelId, eventId)
                .execute();
    }
}
