package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AuthVendorSSOConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean allowedSso;

    public Boolean getAllowedSso() {
        return allowedSso;
    }

    public void setAllowedSso(Boolean allowedSso) {
        this.allowedSso = allowedSso;
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
