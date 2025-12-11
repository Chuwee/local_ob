package es.onebox.event.events.domain.eventconfig;

import java.io.Serializable;

public class AccommodationsConfig implements Serializable {

    private static final long serialVersionUID = 5964656603708968115L;

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
