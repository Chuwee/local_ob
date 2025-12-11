package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class ChannelLiterals extends ArrayList<ChannelLiteral> {

    private static final long serialVersionUID = 1L;

    public ChannelLiterals(Collection<ChannelLiteral> in) {
        super(in);
    }

    public ChannelLiterals() {
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
