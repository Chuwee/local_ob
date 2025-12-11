package es.onebox.event.catalog.dto.product;

import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductCatalogSessionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> sessionIds;
    private SelectionType sessionSelectionType;
    private Set<Long> eventDeliveryPoints;
    private Map<Long, Set<Long>> sessionDeliveryPoints;

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public SelectionType getSessionSelectionType() {
        return sessionSelectionType;
    }

    public void setSessionSelectionType(SelectionType sessionSelectionType) {
        this.sessionSelectionType = sessionSelectionType;
    }

    public Set<Long> getEventDeliveryPoints() {
        return eventDeliveryPoints;
    }

    public void setEventDeliveryPoints(Set<Long> eventDeliveryPoints) {
        this.eventDeliveryPoints = eventDeliveryPoints;
    }

    public Map<Long, Set<Long>> getSessionDeliveryPoints() {
        return sessionDeliveryPoints;
    }

    public void setSessionDeliveryPoints(Map<Long, Set<Long>> sessionDeliveryPoints) {
        this.sessionDeliveryPoints = sessionDeliveryPoints;
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

