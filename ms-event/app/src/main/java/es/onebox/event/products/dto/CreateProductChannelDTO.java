package es.onebox.event.products.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateProductChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 10)
    @NotEmpty(message = "channelIds can not be empty")
    private List<Integer> channelIds;

    public List<Integer> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Integer> channelIds) {
        this.channelIds = channelIds;
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

