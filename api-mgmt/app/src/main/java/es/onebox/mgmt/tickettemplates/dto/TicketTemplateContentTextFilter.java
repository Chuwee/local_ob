package es.onebox.mgmt.tickettemplates.dto;

import es.onebox.mgmt.common.CommunicationElementFilter;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateTagTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketTemplateContentTextFilter extends CommunicationElementFilter<TicketTemplateTagTextType> {

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
