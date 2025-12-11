package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.entity.dto.Provider;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class InventoryProviderConfigResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8757290473908376073L;
    @JsonProperty("inventory_providers")
    private List<Provider> inventoryProviders;

    public List<Provider> getInventoryProviders() {
        return inventoryProviders;
    }

    public void setInventoryProviders(List<Provider> inventoryProviders) {
        this.inventoryProviders = inventoryProviders;
    }
}
