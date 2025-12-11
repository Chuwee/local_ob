package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProducerInvoicePrefixRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("default")
    @NotNull(message = "default value can not be null")
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
