package es.onebox.mgmt.entities.factory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class InventoryProviderFactorySupport {

    @Autowired
    protected InventoryProviderServiceRouter inventoryProviderServiceRouter;


    protected InventoryProviderService getExternalInventoryProviderService(Long entityId, String provider) {
        InventoryProviderEnum integrationServiceType = inventoryProviderServiceRouter.getIntegrationService(entityId, provider);
        return getIntegrationService(integrationServiceType);
    }

    protected final InventoryProviderService getIntegrationService(InventoryProviderEnum integrationServiceType) {
        InventoryProviderService inventoryProviderService = switch (integrationServiceType) {
            case SGA -> this.getSgaIntegration();
            case ITALIAN_COMPLIANCE -> this.getItalianComplianceIntegration();
            default -> this.getOneboxIntegration();
        };

        return inventoryProviderService;
    }

    protected abstract InventoryProviderService getSgaIntegration();

    protected abstract InventoryProviderService getItalianComplianceIntegration();

    protected abstract InventoryProviderService getOneboxIntegration();
}
