package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.datasources.common.enums.StreamingVendor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionStreamingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private StreamingVendor vendor;

    private String value;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public StreamingVendor getVendor() {
        return vendor;
    }

    public void setVendor(StreamingVendor vendor) {
        this.vendor = vendor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
