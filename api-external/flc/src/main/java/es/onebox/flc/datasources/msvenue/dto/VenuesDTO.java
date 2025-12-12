package es.onebox.flc.datasources.msvenue.dto;

import es.onebox.common.datasources.common.dto.Metadata;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VenuesDTO extends BaseResponseCollection<VenueDTO, Metadata> {

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
