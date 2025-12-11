package es.onebox.mgmt.datasources.ms.ticket.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class CapacityBulk<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> ids;

    private List<T> values;

    public CapacityBulk(List<Long> sessionIds, List<T> values) {
        this.ids = sessionIds;
        this.values = values;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
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
