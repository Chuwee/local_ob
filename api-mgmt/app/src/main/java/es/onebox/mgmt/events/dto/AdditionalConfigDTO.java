package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.events.enums.EventAvetConfigType;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1795790261070370692L;

    @JsonProperty("avet_config")
    private EventAvetConfigType avetConfig;

    @JsonProperty("avet_competition_id")
    private Integer avetCompetitionId;

    @JsonProperty("venue_template_id")
    private Long venueTemplateId;

    @JsonProperty("inventory_provider")
    private InventoryProviderEnum inventoryProvider;

    @JsonProperty("external_event_id")
    private String externalEventId;

    private Boolean standalone;

    public AdditionalConfigDTO() {
    }

    public AdditionalConfigDTO(EventAvetConfigType avetConfig, Integer avetCompetitionId) {
        this.avetConfig = avetConfig;
        this.avetCompetitionId = avetCompetitionId;
    }

    public EventAvetConfigType getAvetConfig() {
        return avetConfig;
    }

    public void setAvetConfig(EventAvetConfigType avetConfig) {
        this.avetConfig = avetConfig;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Integer getAvetCompetitionId() {
        return avetCompetitionId;
    }

    public void setAvetCompetitionId(Integer avetCompetitionId) {
        this.avetCompetitionId = avetCompetitionId;
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

    public Boolean getStandalone() {
        return standalone;
    }

    public void setStandalone(Boolean standalone) {
        this.standalone = standalone;
    }
}
