package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateProducerInvoicePrefix implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean defaultPrefix;

    public Boolean getDefaultPrefix() {
        return defaultPrefix;
    }

    public void setDefaultPrefix(Boolean defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
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
