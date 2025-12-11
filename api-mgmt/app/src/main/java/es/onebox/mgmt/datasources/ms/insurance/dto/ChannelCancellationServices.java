package es.onebox.mgmt.datasources.ms.insurance.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelCancellationServices implements Serializable {

    @Serial
    private static final long serialVersionUID = 8041933103370783547L;

    private List<CancellationService> cancellationServices;

    public List<CancellationService> getCancellationServices() {
        return cancellationServices;
    }

    public void setCancellationServices(List<CancellationService> cancellationServices) {
        this.cancellationServices = cancellationServices;
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
