package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EventCommunicationElementBulk implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private List<EventCommunicationElement> values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<EventCommunicationElement> getValues() {
        return values;
    }

    public void setValues(List<EventCommunicationElement> values) {
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
