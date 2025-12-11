package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SettingsLiveStreamingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private Boolean enabled;

    private List<StreamingVendor> vendors;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<StreamingVendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<StreamingVendor> vendors) {
        this.vendors = vendors;
    }
}
