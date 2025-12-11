package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.common.enums.AccommodationsChannelEnablingMode;
import es.onebox.mgmt.datasources.common.enums.AccommodationsVendor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EntityAccommodationsConfig implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<AccommodationsVendor> allowedVendors;
    private AccommodationsChannelEnablingMode channelEnablingMode;
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
}
