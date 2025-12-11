package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.mgmt.sessions.enums.ValidationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionWhitelistDTO extends ListWithMetadata<BarcodeDTO<ValidationStatus>> {

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
