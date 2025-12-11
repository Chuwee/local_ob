package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelReviewConfigResponse extends ListWithMetadata<ChannelReviewConfig> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChannelReviewConfigResponse() {
    }

    public ChannelReviewConfigResponse(List<ChannelReviewConfig> data, Metadata metadata) {
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
