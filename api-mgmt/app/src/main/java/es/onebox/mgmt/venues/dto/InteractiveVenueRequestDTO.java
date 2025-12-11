package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class InteractiveVenueRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8594862501995678250L;

    @NotNull(message = "enabled cannot be null")
    private Boolean enabled;
    @JsonProperty("multimedia_content_code")
    private String multimediaContentCode;
    @JsonProperty("external_minimap_id")
    private String externalMinimapId;
    @JsonProperty("external_plugin_ids")
    private List<Long> externalPluginIds;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getMultimediaContentCode() {
        return multimediaContentCode;
    }

    public void setMultimediaContentCode(String multimediaContentCode) {
        this.multimediaContentCode = multimediaContentCode;
    }

    public String getExternalMinimapId() {
        return externalMinimapId;
    }

    public void setExternalMinimapId(String externalMinimapId) {
        this.externalMinimapId = externalMinimapId;
    }

    public List<Long> getExternalPluginIds() {
        return externalPluginIds;
    }

    public void setExternalPluginIds(List<Long> externalPluginIds) {
        this.externalPluginIds = externalPluginIds;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
