package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1795790261070370692L;

    @JsonProperty("venue_template_id")
    private Long venueTemplateId;

    @JsonProperty("inventory_provider")
    private InventoryProviderEnum inventoryProvider;

    @JsonProperty("external_event_id")
    private String externalEventId;

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public InventoryProviderEnum getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(InventoryProviderEnum inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public String getExternalEventId() {
        return externalEventId;
    }

    public void setExternalEventId(String externalEventId) {
        this.externalEventId = externalEventId;
    }
}
