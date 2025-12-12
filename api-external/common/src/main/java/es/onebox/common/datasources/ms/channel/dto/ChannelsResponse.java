package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelsResponse extends ListWithMetadata<ChannelDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChannelsResponse() {
    }

    public ChannelsResponse(List<ChannelDTO> data, Metadata metadata) {
        super.setData(data);
        super.setMetadata(metadata);
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
