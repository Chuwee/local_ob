package es.onebox.mgmt.seasontickets.dto.presales;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("inventory_provider")
    private InventoryProviderEnum inventoryProvider;

    @JsonProperty("external_presale_id")
    private String externalPresaleId;

    @JsonProperty("entity_id")
    private Long entityId;

    public InventoryProviderEnum getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(InventoryProviderEnum inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public String getExternalPresaleId() {
        return externalPresaleId;
    }

    public void setExternalPresaleId(String externalPresaleId) {
        this.externalPresaleId = externalPresaleId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
