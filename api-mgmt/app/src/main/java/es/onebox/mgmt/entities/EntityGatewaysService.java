package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.entities.converter.GatewaysConverter;
import es.onebox.mgmt.entities.dto.EntityGatewayConfigDTO;
import es.onebox.mgmt.entities.dto.GatewayConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityGatewaysService {

    @Autowired
    private ApiPaymentDatasource apiPaymentDatasource;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private SecurityManager securityManager;

    public List<GatewayConfigDTO> getAvailableGateways(Long entityId) {
        if (entityId == null || entityId <= 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ENTITY_ID_INVALID);
        }
        securityManager.checkEntityAccessible(entityId);
        Operator operator = entitiesRepository.getCachedOperator(entityId);

        List<String> operatorGateways = new ArrayList<>();
        if (operator != null) {
            operatorGateways.addAll(operator.getGateways());
            if (operator.getWallets() != null) {
                operatorGateways.addAll(
                        operator.getWallets().stream().map(WalletConfigDTO::getWallet).toList());
            }
        }

        List<GatewayConfig> gateways = apiPaymentDatasource.getGateways();
        gateways = gateways.stream()
                .filter(g -> operatorGateways.contains(g.getSid()))
                .collect(Collectors.toList());

        return GatewaysConverter.fromApiPayment(gateways, operator.getWallets());
    }

    public List<EntityGatewayConfigDTO> getEntityGatewaysConfig(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        return GatewaysConverter.toDTO(entitiesRepository.getEntityGatewaysConfig(entityId));
    }
}
