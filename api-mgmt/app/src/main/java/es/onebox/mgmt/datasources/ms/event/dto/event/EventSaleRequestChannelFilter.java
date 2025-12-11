package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class EventSaleRequestChannelFilter extends BaseRequestFilter {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long entityId;
    private List<ChannelSubtype> type;
    private String name;
    private Boolean includeThirdPartyChannels;
    private List<Long> visibleEntities;
    private Long operatorId;

    private String destinationChannelType;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

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

    public List<Long> getVisibleEntities() {
        return visibleEntities;
    }

    public void setVisibleEntities(List<Long> visibleEntities) {
        this.visibleEntities = visibleEntities;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
