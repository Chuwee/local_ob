package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;


import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MsUpdateSaleRequestResponseDTO implements Serializable{

    private static final long serialVersionUID = -8039952694070260457L;

    private MsSaleRequestsStatus status;

    public MsSaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(MsSaleRequestsStatus status) {
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
