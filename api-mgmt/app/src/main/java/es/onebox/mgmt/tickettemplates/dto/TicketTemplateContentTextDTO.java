package es.onebox.mgmt.tickettemplates.dto;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateTagTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketTemplateContentTextDTO extends CommunicationElementTextDTO<TicketTemplateTagTextType> {

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
