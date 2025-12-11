package es.onebox.mgmt.events.dto;

import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EventTicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private EventTicketTemplateType type;
    private TicketTemplateFormat format;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventTicketTemplateType getType() {
        return type;
    }

    public void setType(EventTicketTemplateType type) {
        this.type = type;
    }

    public TicketTemplateFormat getFormat() {
        return format;
    }

    public void setFormat(TicketTemplateFormat format) {
        this.format = format;
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
