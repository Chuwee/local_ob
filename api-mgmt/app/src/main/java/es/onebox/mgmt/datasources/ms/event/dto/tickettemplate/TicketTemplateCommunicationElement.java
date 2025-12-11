package es.onebox.mgmt.datasources.ms.event.dto.tickettemplate;

import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketTemplateCommunicationElement extends BaseCommunicationElement {

    private static final long serialVersionUID = 1L;

    private TicketTemplateTagType tagType;

    public TicketTemplateTagType getTagType() {
        return tagType;
    }

    public void setTagType(TicketTemplateTagType tagType) {
        this.tagType = tagType;
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
