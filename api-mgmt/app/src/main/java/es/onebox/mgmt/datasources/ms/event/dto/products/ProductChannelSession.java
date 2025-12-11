package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionDate;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannelSession extends IdNameDTO implements Serializable {

    private SessionDate dates;
    private SessionStatus status;

    @Serial
    private static final long serialVersionUID = -1431240854512256098L;

    public SessionDate getDates() {
        return dates;
    }

    public void setDates(SessionDate dates) {
        this.dates = dates;
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

