package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionSaleRequestDTO extends BaseSessionSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SessionType type;
    private SessionStatus status;

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
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
