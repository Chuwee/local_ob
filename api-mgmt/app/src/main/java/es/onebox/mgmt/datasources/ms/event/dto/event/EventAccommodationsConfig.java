package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.datasources.common.enums.AccommodationsVendor;

import java.io.Serial;
import java.io.Serializable;

public class EventAccommodationsConfig implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private AccommodationsVendor vendor;
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
