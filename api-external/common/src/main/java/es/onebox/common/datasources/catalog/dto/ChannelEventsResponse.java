package es.onebox.common.datasources.catalog.dto;

import es.onebox.common.datasources.common.dto.Metadata;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelEventsResponse extends BaseResponseCollection<ChannelEvent, Metadata> {


    private static final long serialVersionUID = -6446039164342661160L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
