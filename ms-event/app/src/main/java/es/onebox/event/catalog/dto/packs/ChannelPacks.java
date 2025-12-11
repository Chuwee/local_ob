package es.onebox.event.catalog.dto.packs;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPacks extends BaseResponseCollection<ChannelPack, Metadata> implements Serializable {

    @Serial
    private static final long serialVersionUID = -223660303426417272L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
