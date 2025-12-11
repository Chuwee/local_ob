package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class UpdateSessionTicketContentsBulk implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private Set<TicketCommunicationElement> values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Set<TicketCommunicationElement> getValues() {
        return values;
    }

    public void setValues(Set<TicketCommunicationElement> values) {
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
