package es.onebox.mgmt.salerequests.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class BaseSessionSaleRequestDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SessionDate date;

    public SessionDate getDate() {
        return date;
    }

    public void setDate(SessionDate date) {
        this.date = date;
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
