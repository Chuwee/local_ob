package es.onebox.common.datasources.ms.channel.filter;

import es.onebox.common.datasources.ms.event.enums.ChannelSubtype;
import es.onebox.common.datasources.ms.event.enums.ChannelType;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer operatorId;
    private Integer entityId;
    private ChannelType type;
    private ChannelSubtype subtype;
    private String name;
    private String nameEquals;
    private String url;
    private Boolean includeThirdPartyChannels;
    private List<Long> visibleEntities;
    private List<Long> channelIds;
    private SortOperator<String> sort;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public ChannelSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelSubtype subtype) {
        this.subtype = subtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEquals() {
        return nameEquals;
    }

    public void setNameEquals(String nameEquals) {
        this.nameEquals = nameEquals;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
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
