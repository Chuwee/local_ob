package es.onebox.mgmt.datasources.ms.venue.dto.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public class NotNumberedZoneFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Long> id;

    public NotNumberedZoneFilter(Set<Long> id) {
        this.id = id;
    }

    public NotNumberedZoneFilter() {
    }

    public Set<Long> getId() {
        return id;
    }

    public void setId(Set<Long> id) {
        this.id = id;
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
