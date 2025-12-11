package es.onebox.mgmt.datasources.ms.channel.dto.faqs;

import es.onebox.mgmt.channels.faqs.dto.ChannelFAQDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ChannelFAQs extends ArrayList<ChannelFAQ> {

    public ChannelFAQs(Collection<ChannelFAQ> data) {
        super(data);
    }

    public ChannelFAQs() {
    }

    @Serial
    private static final long serialVersionUID = 178872472496919253L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
