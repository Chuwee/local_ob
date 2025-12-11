package es.onebox.mgmt.datasources.ms.venue.dto.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class InteractiveVenue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private String multimediaContentCode;
    private String externalMinimapId;
    private List<ExternalPlugin> externalPlugins;

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

    public List<ExternalPlugin> getExternalPlugins() {
        return externalPlugins;
    }

    public void setExternalPlugins(List<ExternalPlugin> externalPlugins) {
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
