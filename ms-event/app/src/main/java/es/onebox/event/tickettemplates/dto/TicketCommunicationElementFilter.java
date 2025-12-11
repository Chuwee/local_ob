package es.onebox.event.tickettemplates.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.communicationelements.dto.CommunicationElementFilter;

public class TicketCommunicationElementFilter extends CommunicationElementFilter<TicketTemplateTagType>{

    private static final long serialVersionUID = 1L;
    
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
