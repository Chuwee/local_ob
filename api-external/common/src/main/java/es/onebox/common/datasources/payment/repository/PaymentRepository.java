package es.onebox.common.datasources.payment.repository;


import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.payment.ApiPaymentDatasource;
import es.onebox.common.datasources.payment.dto.ChannelGatewayConfig;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentRepository {

    private final ApiPaymentDatasource apiPaymentDatasource;

    @Autowired
    public PaymentRepository(ApiPaymentDatasource apiPaymentDatasource) {
        this.apiPaymentDatasource = apiPaymentDatasource;
    }

    public PaymentOrder getPaymentOrder(String orderCode) {
        return apiPaymentDatasource.getPaymentOrder(orderCode);
    }

    @Cached(key = "getChannelGatewayConfigs", expires = 60 * 10)
    public List<ChannelGatewayConfig> getChannelGatewayConfigs(@CachedArg Long channelId) {
        return apiPaymentDatasource.getChannelGatewayConfigs(channelId);
    }

}
