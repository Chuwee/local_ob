package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AuthVendor {

    private Boolean enabled;

    @JsonProperty("vendor_id")
    private List<String> vendorId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getVendorId() {
        return vendorId;
    }

    public void setVendorId(List<String> vendorId) {
        this.vendorId = vendorId;
    }

}
