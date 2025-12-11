package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.communicationelements.dto.CommunicationElementDTO;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;

public class EmailCommunicationElementDTO extends CommunicationElementDTO<EmailCommunicationElementTagType> {

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
