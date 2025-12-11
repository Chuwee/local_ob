package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateSaleRequestResponseDTO implements Serializable{

    private static final long serialVersionUID = 620166360601462445L;

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
