package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentItemType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionTicketContentsUpdateImagesBulkDTO<T extends List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>>> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids must not be null")
    private List<Long> ids;
    @Valid
    @NotEmpty
    @NotNull(message = "values must not be null")
    private T values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public T getValues() {
        return values;
    }

    public void setValues(T values) {
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

