package es.onebox.mgmt.datasources.ms.insurance.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelCancellationServicesUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CancellationService> cancellationServices;
    private Long operatorId;

    public List<CancellationService> getCancellationServices() {
        return cancellationServices;
    }

    public void setCancellationServices(List<CancellationService> cancellationServices) {
        this.cancellationServices = cancellationServices;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
