package es.onebox.common.datasources.ms.channel;

import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelConfigResponse extends ListWithMetadata<ChannelConfigDTO> {

    @Serial
    private static final long serialVersionUID = 374711505697919069L;

    public ChannelConfigResponse () {
    }

    public ChannelConfigResponse(List<ChannelConfigDTO> data, Metadata metadata) {
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

