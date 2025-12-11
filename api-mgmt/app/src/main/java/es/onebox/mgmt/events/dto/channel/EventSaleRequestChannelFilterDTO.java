package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class EventSaleRequestChannelFilterDTO extends BaseRequestFilter {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<ChannelSubtype> type;
    private String name;
    @JsonProperty("include_third_party_channels")
    private Boolean includeThirdPartyChannels;
    @JsonProperty("destination_channel_type")
    private String destinationChannelType;


    public List<ChannelSubtype> getType() {
        return type;
    }

    public void setType(List<ChannelSubtype> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIncludeThirdPartyChannels() {
        return includeThirdPartyChannels;
    }

    public void setIncludeThirdPartyChannels(Boolean includeThirdPartyChannels) {
        this.includeThirdPartyChannels = includeThirdPartyChannels;
    }

    public String getDestinationChannelType() {
        return destinationChannelType;
    }
    public void setDestinationChannelType(String destinationChannelType) {
        this.destinationChannelType = destinationChannelType;
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
