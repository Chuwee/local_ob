package es.onebox.event.catalog.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelCatalogEventsResponse extends ListWithMetadata<ChannelCatalogEventDTO> {

    private static final long serialVersionUID = -4111447824817083197L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
