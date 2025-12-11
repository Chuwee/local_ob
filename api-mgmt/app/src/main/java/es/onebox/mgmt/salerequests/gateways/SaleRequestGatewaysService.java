package es.onebox.mgmt.salerequests.gateways;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilter;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilterParam;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.salerequests.gateways.dto.GatewayConfigUpdateRequestDTO;
import es.onebox.mgmt.salerequests.gateways.dto.SaleRequestGatewayConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.GatewayBenefitsConfigService;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleRequestGatewaysService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final SecurityManager securityManager;
    private final ApiPaymentDatasource apiPaymentDatasource;
    private final GatewayBenefitsConfigService gatewayBenefitsConfigService;
    private final EntitiesRepository entitiesRepository;

    private final String EVENT_FILTER = "event_";

    @Autowired
    public SaleRequestGatewaysService(SaleRequestsRepository saleRequestsRepository, SecurityManager securityManager,
                                      ApiPaymentDatasource apiPaymentDatasource, GatewayBenefitsConfigService gatewayBenefitsConfigService,
                                      EntitiesRepository entitiesRepository){
        this.saleRequestsRepository = saleRequestsRepository;
        this.securityManager = securityManager;
        this.apiPaymentDatasource = apiPaymentDatasource;
        this.gatewayBenefitsConfigService = gatewayBenefitsConfigService;
        this.entitiesRepository = entitiesRepository;
    }


    public SaleRequestGatewayConfigDTO getSaleRequestGatewayConfiguration(Long saleRequestId){
        MsSaleRequestDTO saleRequest = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);
        Entity entity = entitiesRepository.getCachedEntity(saleRequest.getChannel().getEntity().getId());

        String filter = EVENT_FILTER + saleRequest.getEvent().getId();
        ChannelGatewayConfigFilter channelGatewayConfigFilter = apiPaymentDatasource.getChannelGatewayConfigFilter(saleRequest.getChannel().getId(), filter);
        List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(saleRequest.getChannel().getId());

        List<GatewayBenefitsConfigDTO> benefitsConfigs = gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId);
        SaleRequestGatewayConfigDTO result = SaleRequestGatewaysConverter.toSaleRequestGatewayConfigDTO(channelGatewayConfigFilter, channelGatewayConfigs, benefitsConfigs);
        result.setBenefits(BooleanUtils.isTrue(entity.getAllowGatewayBenefits()));

        return result;
    }

    public void updateSaleRequestGatewayConfiguration(Long saleRequestId, GatewayConfigUpdateRequestDTO request){
        MsSaleRequestDTO saleRequest = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(saleRequest.getChannel().getId());
        SaleRequestsValidations.validateGatewayConfigUpdateRequest(request, channelGatewayConfigs);

        String filter = EVENT_FILTER + saleRequest.getEvent().getId();
        if(Boolean.TRUE.equals(request.getCustom())){
            ChannelGatewayConfigFilterParam channelGatewayConfigFilterParam = SaleRequestGatewaysConverter.toChannelGatewayConfigFilterParam(request, saleRequest.getChannel().getId());
            apiPaymentDatasource.updateChannelGatewayConfigFilter(saleRequest.getChannel().getId(), filter, channelGatewayConfigFilterParam);
        } else {
            apiPaymentDatasource.deleteChannelGatewayConfigFilter(saleRequest.getChannel().getId(), filter);
        }
    }
}
