package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

public class CapacityExternalDTO extends IdNameDTO implements Serializable {

    @JsonProperty("available")
    private Boolean available;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("loaded")
    private Boolean loaded;

    @JsonProperty("seasonId")
    private Byte seasonId;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLoaded() {
        return loaded;
    }

    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    public Byte getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Byte seasonId) {
        this.seasonId = seasonId;
    }
}
