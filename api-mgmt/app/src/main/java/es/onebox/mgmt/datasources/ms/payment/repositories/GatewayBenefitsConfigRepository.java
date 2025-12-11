package es.onebox.mgmt.datasources.ms.payment.repositories;

import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.GatewayBenefitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GatewayBenefitsConfigRepository {

    private final ApiPaymentDatasource apiPaymentDatasource;

    @Autowired
    public GatewayBenefitsConfigRepository(ApiPaymentDatasource apiPaymentDatasource) {
        this.apiPaymentDatasource = apiPaymentDatasource;
    }

    public GatewayBenefitConfiguration createGatewayBenefitConfiguration(GatewayBenefitConfiguration configuration) {
        return this.apiPaymentDatasource.createGatewayBenefitConfiguration(configuration);
    }

    public List<GatewayBenefitConfiguration> getListGatewayBenefitConfigurations(Long channelId, Long eventId) {
        return this.apiPaymentDatasource.getListGatewayBenefitConfigurations(channelId, eventId);
    }

    public GatewayBenefitConfiguration getGatewayBenefitConfiguration(String gatewaySid, String confSid,Long channelId, Long eventId) {
        return this.apiPaymentDatasource.getGatewayBenefitConfiguration(gatewaySid, confSid,channelId, eventId);
    }

    public GatewayBenefitConfiguration updateGatewayBenefitConfiguration(String gatewaySid, String confSid,Long channelId, Long eventId,
                                                                         GatewayBenefitConfiguration patchData) {
        return this.apiPaymentDatasource.updateGatewayBenefitConfiguration(gatewaySid, confSid, channelId, eventId, patchData);
    }

    public void deleteGatewayBenefitConfiguration(String gatewaySid, String confSid, Long channelId,  Long eventId) {
        this.apiPaymentDatasource.deleteGatewayBenefitConfiguration(gatewaySid, confSid, channelId, eventId);
    }
}
