package es.onebox.mgmt.events.dto.channel;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class EventChannelContentSessionsLinksResponse extends ListWithMetadata<EventChannelContentSessionLink> {

    @Serial
    private static final long serialVersionUID = -178310780857602981L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
