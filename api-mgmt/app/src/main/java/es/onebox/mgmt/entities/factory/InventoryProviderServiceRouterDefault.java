package es.onebox.mgmt.entities.factory;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryProviderServiceRouterDefault implements InventoryProviderServiceRouter {

    private final static String ONEBOX = "onebox";
    private final static String SEETICKETS = "seetickets";
    private final static String SGA = "sga";
    private final static String ITALIAN_COMPLIANCE = "italian_compliance";

    private final EntitiesRepository entitiesRepository;

    @Autowired
    public InventoryProviderServiceRouterDefault(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public InventoryProviderEnum getIntegrationService(Long entityId, String provider) {
        if(provider == null) {
            return InventoryProviderEnum.ONEBOX;
        }
        return switch (provider) {
			case SGA, ITALIAN_COMPLIANCE -> this.getIntegrationServiceEnum(entityId, provider);
            default -> InventoryProviderEnum.ONEBOX;
        };
    }

    public InventoryProviderEnum getIntegrationServiceEnum(Long entityId, String provider) {

        ExternalConfig externalConfig = StringUtils.isBlank(provider) ? null : entitiesRepository.getExternalConfig(entityId);

        return getAndValidateInventoryProvider(externalConfig, provider);
    }

    public InventoryProviderEnum getAndValidateInventoryProvider(ExternalConfig externalConfig, String providerId) {
        if(StringUtils.isBlank(providerId)){
            return null;
        }

        if(externalConfig == null){
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_EXTERNAL_CONFIG_NOT_FOUND);
        }

        if (CollectionUtils.isEmpty(externalConfig.getInventoryProviders())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_EXTERNAL_INVENTORY_PROVIDER_NOT_RELATED);
        }

        es.onebox.mgmt.datasources.ms.entity.dto.Provider provider = externalConfig.getInventoryProviders()
                .stream()
                .filter(e->e.name().equalsIgnoreCase(providerId))
                .findFirst()
                .orElseThrow(()-> new OneboxRestException(ApiMgmtErrorCode.ENTITY_EXTERNAL_INVENTORY_PROVIDER_NOT_RELATED));

        return InventoryProviderEnum.getByCode(provider.name());
    }
}
