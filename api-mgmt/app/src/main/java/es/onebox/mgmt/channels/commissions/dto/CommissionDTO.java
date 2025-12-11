package es.onebox.mgmt.channels.commissions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.RangeDTO;

import java.io.Serializable;
import java.util.List;

public class CommissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommissionTypeDTO type;
    @JsonProperty("enabled_ranges")
    private Boolean enabledRanges;
    private List<RangeDTO> ranges;

    public CommissionDTO() {
    }

    public CommissionDTO(CommissionTypeDTO type, List<RangeDTO> ranges, Boolean enabledRanges) {
        this.type = type;
        this.ranges = ranges;
        this.enabledRanges = enabledRanges;
    }

    public List<RangeDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeDTO> ranges) {
        this.ranges = ranges;
    }

    public CommissionTypeDTO getType() {
        return type;
    }

    public void setType(CommissionTypeDTO type) {
        this.type = type;
    }

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
    }
}
