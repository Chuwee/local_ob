package es.onebox.mgmt.salerequests.gateways;

import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDTO;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilter;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilterParam;
import es.onebox.mgmt.datasources.ms.payment.dto.Key;
import es.onebox.mgmt.salerequests.gateways.dto.GatewayConfigUpdateRequestDTO;
import es.onebox.mgmt.salerequests.gateways.dto.SaleRequestGatewayConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SaleRequestGatewaysConverter {
    private SaleRequestGatewaysConverter() {throw new UnsupportedOperationException("Cannot instantiate converter class");}

    public static SaleRequestGatewayConfigDTO toSaleRequestGatewayConfigDTO(ChannelGatewayConfigFilter channelGatewayConfigFilter,
                                                                     List<ChannelGatewayConfig> channelGatewayConfigs,
                                                                     List<GatewayBenefitsConfigDTO> benefitsConfigs){

        SaleRequestGatewayConfigDTO saleRequestGatewayConfig = new SaleRequestGatewayConfigDTO();

        if (Objects.nonNull(channelGatewayConfigFilter)){
            saleRequestGatewayConfig.setCustom(true);
            saleRequestGatewayConfig.setChannelGateways(channelGatewayConfigs.stream()
                    .map(gateway -> toChannelGatewayDTO(gateway, channelGatewayConfigFilter, benefitsConfigs))
                    .collect(Collectors.toList()));
        } else {
            saleRequestGatewayConfig.setCustom(false);
            saleRequestGatewayConfig.setChannelGateways(channelGatewayConfigs.stream()
                    .map(gateway -> toChannelGatewayDTO(gateway, benefitsConfigs))
                    .collect(Collectors.toList()));
        }

        return saleRequestGatewayConfig;
    }

    public static ChannelGatewayConfigFilterParam toChannelGatewayConfigFilterParam(GatewayConfigUpdateRequestDTO request, Long channelId){
        ChannelGatewayConfigFilterParam result = new ChannelGatewayConfigFilterParam();
        List<Key> gatewaysIds = new ArrayList<>();

        request.getChannelGateways().stream().forEach(gateway -> {
            if(Boolean.TRUE.equals(gateway.getActive())){
                Key activeGateway = new Key(channelId.intValue(), gateway.getGatewaySid(), gateway.getConfigurationSid());
                gatewaysIds.add(activeGateway);

                if (Boolean.TRUE.equals(gateway.getDefaultGateway())){
                    result.setGatewayDefault(activeGateway);
                }
            }
        });
        result.setGatewaysIds(gatewaysIds);

        return result;
    }

    private static ChannelGatewayDTO toChannelGatewayDTO(ChannelGatewayConfig channelGatewayConfig, List<GatewayBenefitsConfigDTO> benefitsConfigs) {
        ChannelGatewayDTO result = new ChannelGatewayDTO();

        result.setActive(false);
        result.setDefaultGateway(false);
        result.setConfigurationSid(channelGatewayConfig.getConfSid());
        result.setGatewaySid(channelGatewayConfig.getGatewaySid());
        result.setDescription(channelGatewayConfig.getDescription());
        result.setName(channelGatewayConfig.getInternalName());
        result.setHasBenefits(hasGatewayBenefits(channelGatewayConfig, benefitsConfigs));
        result.setAllowBenefits(BooleanUtils.isTrue(channelGatewayConfig.isAllowBenefits()));
        return result;
    }

    private static ChannelGatewayDTO toChannelGatewayDTO(ChannelGatewayConfig gatewayConfig, ChannelGatewayConfigFilter channelGatewayConfigFilter, List<GatewayBenefitsConfigDTO> benefitsConfigs) {
        ChannelGatewayDTO channelGateway = toChannelGatewayDTO(gatewayConfig, benefitsConfigs);

        if(CollectionUtils.isNotEmpty(channelGatewayConfigFilter.getGatewaysIds())){
            channelGateway.setActive(channelGatewayConfigFilter.getGatewaysIds().stream().anyMatch(g -> channelGateway.getConfigurationSid().equals(g.getConfSid())));
        }

        if(Objects.nonNull(channelGatewayConfigFilter.getGatewayDefault()) &&
                channelGatewayConfigFilter.getGatewayDefault().getConfSid().equals(channelGateway.getConfigurationSid())) {
            channelGateway.setDefaultGateway(true);
        }

        return channelGateway;
    }

    private static Boolean hasGatewayBenefits(ChannelGatewayConfig gatewayConfig, List<GatewayBenefitsConfigDTO> benefitsConfigs) {
        if (CollectionUtils.isEmpty(benefitsConfigs) && CollectionUtils.isEmpty(benefitsConfigs)) {
            return false;
        }

        return benefitsConfigs.stream()
                .anyMatch(config -> 
                    gatewayConfig.getGatewaySid().equals(config.getGatewaySid()) &&
                    gatewayConfig.getConfSid().equals(config.getConfSid()) &&
                    config.getBenefits() != null && 
                    !config.getBenefits().isEmpty()
                );
    }
}
