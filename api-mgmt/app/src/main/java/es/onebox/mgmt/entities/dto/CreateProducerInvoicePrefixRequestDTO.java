package es.onebox.mgmt.entities.dto;

import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateProducerInvoicePrefixRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Pattern(regexp = "^[A-Z\\d_/#-]{1,10}+$", message = "The invoice prefix only allows the following characters: A-Z, 0-9, -, _, #, /")
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
