package es.onebox.mgmt.entities.factory;


public interface InventoryProviderServiceRouter {

    InventoryProviderEnum getIntegrationService(Long entityId, String provider);

}
