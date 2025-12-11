package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MsUpdateSaleRequestDTO implements Serializable{

    private static final long serialVersionUID = -8628372548319297105L;

    private MsSaleRequestsStatus status;
    private Integer userId;

    public MsSaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(MsSaleRequestsStatus status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
