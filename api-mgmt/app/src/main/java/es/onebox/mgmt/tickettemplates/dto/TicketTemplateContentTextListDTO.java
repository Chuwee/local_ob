package es.onebox.mgmt.tickettemplates.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class TicketTemplateContentTextListDTO extends ArrayList<TicketTemplateContentTextDTO> {

    private static final long serialVersionUID = 1L;

    public TicketTemplateContentTextListDTO() {
    }

    public TicketTemplateContentTextListDTO(Collection<? extends TicketTemplateContentTextDTO> c) {
        super(c);
    }

    public TicketTemplateContentTextListDTO(int initialCapacity) {
        super(initialCapacity);
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
