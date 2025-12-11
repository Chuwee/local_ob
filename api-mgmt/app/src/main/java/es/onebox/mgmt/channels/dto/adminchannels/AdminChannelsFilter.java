package es.onebox.mgmt.channels.dto.adminchannels;


import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.channels.dto.ChannelsFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@MaxLimit(20)
@DefaultLimit(20)
public class AdminChannelsFilter extends ChannelsFilter implements Serializable {

    private static final long serialVersionUID = 1;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
