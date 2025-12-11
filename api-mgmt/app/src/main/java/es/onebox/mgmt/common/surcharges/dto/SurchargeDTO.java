package es.onebox.mgmt.common.surcharges.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.RangeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SurchargeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SurchargeTypeDTO type;
    @JsonProperty("enabled_ranges")
    private Boolean enabledRanges;
    private List<RangeDTO> ranges;

    public SurchargeDTO() {
    }

    public SurchargeDTO(SurchargeTypeDTO type, List<RangeDTO> ranges) {
        this.type = type;
        this.ranges = ranges;
    }

    public List<RangeDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeDTO> ranges) {
        this.ranges = ranges;
    }

    public SurchargeTypeDTO getType() {
        return type;
    }

    public void setType(SurchargeTypeDTO type) {
        this.type = type;
    }

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
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
