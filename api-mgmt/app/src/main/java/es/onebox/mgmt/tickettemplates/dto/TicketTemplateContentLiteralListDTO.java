package es.onebox.mgmt.tickettemplates.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class TicketTemplateContentLiteralListDTO extends ArrayList<TicketTemplateContentLiteralDTO> {

    private static final long serialVersionUID = 1L;

    public TicketTemplateContentLiteralListDTO() {
    }

    public TicketTemplateContentLiteralListDTO(Collection<? extends TicketTemplateContentLiteralDTO> c) {
        super(c);
    }

    public TicketTemplateContentLiteralListDTO(int initialCapacity) {
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
