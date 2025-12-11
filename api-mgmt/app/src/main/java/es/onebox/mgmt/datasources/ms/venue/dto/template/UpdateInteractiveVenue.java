package es.onebox.mgmt.datasources.ms.venue.dto.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateInteractiveVenue implements Serializable {

    @Serial
    private static final long serialVersionUID = 453622823634133153L;

    private Boolean enabled;
    private String multimediaContentCode;
    private String externalMinimapId;
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
