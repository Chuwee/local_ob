package es.onebox.event.sessions.dto;

import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class TicketCommunicationElementBulkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private Set<TicketCommunicationElementDTO> values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Set<TicketCommunicationElementDTO> getValues() {
        return values;
    }

    public void setValues(Set<TicketCommunicationElementDTO> values) {
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

