package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductSessionBaseDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8061105847538611953L;

    private SessionDateDTO dates;

    public SessionDateDTO getDates() {
        return dates;
    }

    public void setDates(SessionDateDTO dates) {
        this.dates = dates;
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
