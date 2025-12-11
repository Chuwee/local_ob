package es.onebox.event.events.request;

import es.onebox.event.communicationelements.dto.CommunicationElementFilter;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PassbookCommunicationElementFilter extends CommunicationElementFilter<PassbookCommunicationElementTagType> {

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
