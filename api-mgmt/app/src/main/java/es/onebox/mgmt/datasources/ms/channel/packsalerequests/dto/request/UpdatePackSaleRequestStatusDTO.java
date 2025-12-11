package es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request;

import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackChannelSaleRequestStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdatePackSaleRequestStatusDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = -6424413225075887959L;

    private PackChannelSaleRequestStatus status;

    public PackChannelSaleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PackChannelSaleRequestStatus status) {
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
