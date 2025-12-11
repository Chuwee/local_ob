package es.onebox.mgmt.channels.faqs.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ChannelFAQsDTO extends ArrayList<ChannelFAQDTO> {

    @Serial
    private static final long serialVersionUID = 3950864734761420347L;

    public ChannelFAQsDTO(Collection<ChannelFAQDTO> data) {
        super(data);
    }

    public ChannelFAQsDTO() {
    }

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
