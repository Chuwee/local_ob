package es.onebox.common.datasources.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.payment.dto.ChannelGatewayConfig;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiPaymentDatasource {

    private static final int TIMEOUT = 60000;

    private final HttpClient httpClient;

    @Autowired
    public ApiPaymentDatasource(@Value("${clients.services.api-payment}") String baseUrl,
                                ObjectMapper jacksonMapper) {


        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .readTimeout(TIMEOUT)
                .build();
    }

    public PaymentOrder getPaymentOrder(String orderCode) {
        return httpClient.buildRequest(HttpMethod.GET, "/payment/orders/{orderCode}")
                .pathParams(orderCode)
                .execute(PaymentOrder.class);
    }

    public List<ChannelGatewayConfig> getChannelGatewayConfigs(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/payment/channels/{channelId}/gateways")
                .pathParams(channelId)
                .execute(ListType.of(ChannelGatewayConfig.class));
    }

}
