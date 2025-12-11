package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;

import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("external_capacity_id")
    private Integer capacityId;

    @JsonProperty("inventory_provider")
    private InventoryProviderEnum inventoryProvider;

    @JsonProperty("inventory_id")
    private String inventoryId;

    public AdditionalConfigDTO() {
    }

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
    }

    public InventoryProviderEnum getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(InventoryProviderEnum inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }
}
