package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.datasources.common.dto.Range;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelCommissionType;

import java.io.Serializable;
import java.util.List;

public class ChannelCommission implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChannelCommissionType type;
    private Boolean enabledRanges;
    private List<Range> ranges;

    public ChannelCommissionType getType() {
        return type;
    }

    public void setType(ChannelCommissionType type) {
        this.type = type;
    }

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }
}
