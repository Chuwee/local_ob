package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.common.dto.Metadata;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class EventsDTO extends BaseResponseCollection<EventDTO, Metadata> {

    @Serial
    private static final long serialVersionUID = -5160857697828280820L;

    public EventsDTO() {
    }

    public EventsDTO(List<EventDTO> data, Metadata metadata) {
        super(data, metadata);
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
