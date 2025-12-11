package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelCancellationServicesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("providers")
    private List<CancellationServiceDTO> cancellationServices;

    public List<CancellationServiceDTO> getCancellationServices() {
        return cancellationServices;
    }

    public void setCancellationServices(List<CancellationServiceDTO> cancellationServices) {
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
