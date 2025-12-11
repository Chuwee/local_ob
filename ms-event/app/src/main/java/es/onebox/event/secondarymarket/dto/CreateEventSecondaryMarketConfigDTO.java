package es.onebox.event.secondarymarket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateEventSecondaryMarketConfigDTO extends SecondaryMarketConfigDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<EnabledChannelDTO> enabledChannels;

    public List<EnabledChannelDTO> getEnabledChannels() {
        return enabledChannels;
    }
    public void setEnabledChannels(List<EnabledChannelDTO> enabledChannels) {
        this.enabledChannels = enabledChannels;
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