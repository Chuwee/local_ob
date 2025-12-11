package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityGatewayConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.entities.dto.EntityGatewayConfigDTO;
import es.onebox.mgmt.entities.dto.GatewayConfigDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GatewaysConverter {

    private GatewaysConverter() {
    }

    public static GatewayConfigDTO fromApiPayment(GatewayConfig gateway, List<WalletConfigDTO> wallets) {
        if (gateway == null) {
            return null;
        }
        GatewayConfigDTO dto = new GatewayConfigDTO();
        dto.setGatewaySid(gateway.getSid());
        dto.setName(gateway.getName());
        dto.setDescription(gateway.getDescription());
        dto.setSynchronous(gateway.isSync());
        dto.setRefund(gateway.isRefund());
        dto.setRetry(gateway.isRetry());
        dto.setRetries(gateway.getMaxAttempts());
        dto.setLive(gateway.isLive());
        dto.setWallet(gateway.getWallet() != null ? gateway.getWallet() : Boolean.FALSE);
        if (Boolean.TRUE.equals(gateway.getWallet()) && CollectionUtils.isNotEmpty(wallets)) {
            WalletConfigDTO gatewayWallets = wallets.stream()
                    .filter(w -> w.getWallet().equals(gateway.getSid())).findFirst().orElse(null);
            if (gatewayWallets != null && CollectionUtils.isNotEmpty(gatewayWallets.getGateways())) {
                dto.setAvailableGatewayAsociation(gatewayWallets.getGateways());
            }
        }
        return dto;
    }

    public static List<GatewayConfigDTO> fromApiPayment(List<GatewayConfig> gateways, List<WalletConfigDTO> wallets) {
        if (gateways == null) {
            return new ArrayList<>();
        }
        return gateways.stream()
                .map(g -> fromApiPayment(g, wallets))
                .collect(Collectors.toList());
    }

    public static List<EntityGatewayConfigDTO> toDTO(List<EntityGatewayConfig> in) {
        return in.stream().map(GatewaysConverter::toDTO).toList();
    }

    private static EntityGatewayConfigDTO toDTO(EntityGatewayConfig in) {
        EntityGatewayConfigDTO out = new EntityGatewayConfigDTO();
        out.setName(in.getName());
        out.setFieldValues(in.getFieldValues());
        return out;
    }
}
