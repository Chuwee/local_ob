package es.onebox.mgmt.packsalerequest.dto.request;

import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PackSaleRequestUpdateStatusDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = -6424413225075887959L;

    @NotNull
    private PackSaleRequestStatus status;

    public PackSaleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PackSaleRequestStatus status) {
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
