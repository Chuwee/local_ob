package es.onebox.mgmt.entities.invoiceprovider.dto;

import es.onebox.mgmt.entities.invoiceprovider.enums.InvoiceProvider;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class RequestInvoiceProviderDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "provider can not be null")
    private InvoiceProvider provider;

    public InvoiceProvider getProvider() {
        return provider;
    }

    public void setProvider(InvoiceProvider provider) {
        this.provider = provider;
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
