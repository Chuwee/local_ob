package es.onebox.event.datasources.ms.ticket.dto;

import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionPriceZonesFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> quota;

    private CapacityType type;

    public List<Long> getQuota() {
        return quota;
    }

    public void setQuota(List<Long> quota) {
        this.quota = quota;
    }

    public CapacityType getType() {
        return type;
    }

    public void setType(CapacityType type) {
        this.type = type;
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
