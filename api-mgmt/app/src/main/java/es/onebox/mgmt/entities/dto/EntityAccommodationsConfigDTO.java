package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.accommodations.AccommodationsVendor;
import es.onebox.mgmt.entities.enums.AccommodationsChannelEnablingMode;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EntityAccommodationsConfigDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("allowed_vendors")
    private List<AccommodationsVendor> allowedVendors;

    @JsonProperty("channel_enabling_mode")
    private AccommodationsChannelEnablingMode channelEnablingMode;

    @JsonProperty("enabled_channels")
    private List<Long> enabledChannelIds;

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<AccommodationsVendor> getAllowedVendors() {
        return allowedVendors;
    }
    public void setAllowedVendors(List<AccommodationsVendor> allowedVendors) {
        this.allowedVendors = allowedVendors;
    }

    public AccommodationsChannelEnablingMode getChannelEnablingMode() {
        return channelEnablingMode;
    }
    public void setChannelEnablingMode(AccommodationsChannelEnablingMode channelEnablingMode) {
        this.channelEnablingMode = channelEnablingMode;
    }

    public List<Long> getEnabledChannelIds() {
        return enabledChannelIds;
    }
    public void setEnabledChannelIds(List<Long> enabledChannelIds) {
        this.enabledChannelIds = enabledChannelIds;
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
