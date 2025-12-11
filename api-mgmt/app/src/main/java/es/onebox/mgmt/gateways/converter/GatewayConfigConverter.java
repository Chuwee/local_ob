package es.onebox.mgmt.gateways.converter;

import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.gateways.dto.GatewayConfigDTO;

import java.util.List;
import java.util.stream.Collectors;

public class GatewayConfigConverter {

    private GatewayConfigConverter() {
    }

    public static List<GatewayConfigDTO> fromDTO(List<GatewayConfig> in) {
        return in.stream().map(t -> GatewayConfigConverter.fromDTO(t, null)).collect(Collectors.toList());
    }

    public static GatewayConfigDTO fromDTO(GatewayConfig gatewayConfig, List<String> fields) {
        GatewayConfigDTO target = new GatewayConfigDTO();

        target.setSid(gatewayConfig.getSid());
        target.setName(gatewayConfig.getName());
        target.setRetry(gatewayConfig.isRetry());
        target.setMaxAttempts(gatewayConfig.getMaxAttempts());
        target.setRefund(gatewayConfig.isRefund());
        target.setShowBillingForm(gatewayConfig.isShowBillingForm());
        target.setSaveCardByDefault(gatewayConfig.isSaveCardByDefault());
        target.setForceRiskEvaluation(gatewayConfig.isForceRiskEvaluation());
        target.setSendAdditionalData(gatewayConfig.isSendAdditionalData());
        target.setPriceRangeEnabled(gatewayConfig.isPriceRangeEnabled());
        target.setAllowBenefits((gatewayConfig.getAllowBenefits() != null) ? gatewayConfig.getAllowBenefits() : false);
        target.setLive(gatewayConfig.isLive());
        target.setFields(fields);
        target.setWallet(gatewayConfig.getWallet() != null ? gatewayConfig.getWallet() : Boolean.FALSE);

        return target;
    }
}
