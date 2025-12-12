package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AuthVendorCallbackValidation implements Serializable {

    @Serial
    private static final long serialVersionUID = -4005516527912138140L;

    private Long id;
    private AuthVendorCallbackValidationType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthVendorCallbackValidationType getType() {
        return type;
    }

    public void setType(AuthVendorCallbackValidationType type) {
        this.type = type;
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
