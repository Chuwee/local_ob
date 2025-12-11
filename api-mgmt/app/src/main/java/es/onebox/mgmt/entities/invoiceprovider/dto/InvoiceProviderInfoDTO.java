package es.onebox.mgmt.entities.invoiceprovider.dto;

import es.onebox.mgmt.entities.invoiceprovider.enums.InvoiceProvider;
import es.onebox.mgmt.entities.invoiceprovider.enums.RequestStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class InvoiceProviderInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private InvoiceProvider provider;
    private RequestStatus status;

    public InvoiceProvider getProvider() {
        return provider;
    }

    public void setProvider(InvoiceProvider provider) {
        this.provider = provider;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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
