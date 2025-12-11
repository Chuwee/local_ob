package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long operatorId;
    private Long entityId;
    private Long entityAdminId;
    private List<ChannelType> type;
    private List<ChannelSubtype> subtype;
    private List<ChannelStatus> status;
    private String name;
    private Boolean includeThirdPartyChannels;
    private List<Long> visibleEntities;
    private List<Long> channelIds;
    private Boolean v4Enabled;
    private SortOperator<String> sort;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public List<ChannelType> getType() {
        return type;
    }

    public void setType(List<ChannelType> type) {
        this.type = type;
    }

    public List<ChannelStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ChannelStatus> status) {
        this.status = status;
    }

    public List<ChannelSubtype> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<ChannelSubtype> subtype) {
        this.subtype = subtype;
    }

    public Boolean getIncludeThirdPartyChannels() {
        return includeThirdPartyChannels;
    }

    public void setIncludeThirdPartyChannels(Boolean includeThirdPartyChannels) {
        this.includeThirdPartyChannels = includeThirdPartyChannels;
    }

    public void setVisibleEntities(List<Long> visibleEntities) {
        this.visibleEntities = visibleEntities;
    }

    public List<Long> getVisibleEntities() {
        return visibleEntities;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
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
