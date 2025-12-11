package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@DefaultLimit(50)
public class ChannelsFilter extends BaseEntityRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @JsonProperty("operator_id")
    private Long operatorId;
    private List<ChannelSubtype> type;
    private List<ChannelStatus> status;
    private String name;
    @JsonProperty("include_third_party_channels")
    private Boolean includeThirdPartyChannels;
    private SortOperator<String> sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChannelStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ChannelStatus> status) {
        this.status = status;
    }


    public List<ChannelSubtype> getType() {
        return type;
    }

    public void setType(List<ChannelSubtype> type) {
        this.type = type;
    }

    public Boolean getIncludeThirdPartyChannels() {
        return includeThirdPartyChannels;
    }

    public void setIncludeThirdPartyChannels(Boolean includeThirdPartyChannels) {
        this.includeThirdPartyChannels = includeThirdPartyChannels;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
