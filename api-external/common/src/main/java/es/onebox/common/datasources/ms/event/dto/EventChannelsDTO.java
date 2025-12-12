package es.onebox.common.datasources.ms.event.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class EventChannelsDTO extends BaseResponseCollection<BaseEventChannelDTO, Metadata> {

    @Serial
    private static final long serialVersionUID = 1L;

    public EventChannelsDTO() {
    }

    public EventChannelsDTO(List<BaseEventChannelDTO> data, Metadata metadata) {
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
