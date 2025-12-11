package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateSaleRequestDTO  implements Serializable{

    private static final long serialVersionUID = 9206791672372976703L;

    private SaleRequestsStatus status;

    public SaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(SaleRequestsStatus status) {
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
