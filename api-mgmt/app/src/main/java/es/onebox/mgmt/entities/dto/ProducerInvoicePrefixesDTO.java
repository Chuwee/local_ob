package es.onebox.mgmt.entities.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProducerInvoicePrefixesDTO extends ListWithMetadata<ProducerInvoicePrefixDTO> {

    private static final long serialVersionUID = 1L;

    public ProducerInvoicePrefixesDTO() {
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
