package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class AccommodationsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("vendor")
    private AccommodationsVendor vendor;

    @JsonProperty("value")
    private String value;

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public AccommodationsVendor getVendor() {
        return vendor;
    }
    public void setVendor(AccommodationsVendor vendor) {
        this.vendor = vendor;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
