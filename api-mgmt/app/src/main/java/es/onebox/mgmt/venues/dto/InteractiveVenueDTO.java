package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class InteractiveVenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @JsonProperty("multimedia_content_code")
    private String multimediaContentCode;
    @JsonProperty("external_minimap_id")
    private String externalMinimapId;
    @JsonProperty("external_plugins")
    private List<ExternalPluginDTO> externalPlugins;

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

    public List<ExternalPluginDTO> getExternalPlugins() {
        return externalPlugins;
    }

    public void setExternalPlugins(List<ExternalPluginDTO> externalPlugins) {
        this.externalPlugins = externalPlugins;
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
