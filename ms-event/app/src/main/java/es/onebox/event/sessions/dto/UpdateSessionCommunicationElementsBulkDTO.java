package es.onebox.event.sessions.dto;

import es.onebox.event.events.dto.EventCommunicationElementDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateSessionCommunicationElementsBulkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private List<EventCommunicationElementDTO> values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<EventCommunicationElementDTO> getValues() {
        return values;
    }

    public void setValues(List<EventCommunicationElementDTO> values) {
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

