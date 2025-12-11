package es.onebox.mgmt.producttickettemplate.dto;

import es.onebox.mgmt.common.CommunicationElementFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ProductTicketTemplateContentLiteralFilter extends CommunicationElementFilter<String> {
    @Serial
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
