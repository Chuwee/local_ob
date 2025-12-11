package es.onebox.event.surcharges.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SurchargesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SurchargeTypeDTO type;
    private SurchargeLimitDTO limit;
    private List<RangeDTO> ranges;
    private Boolean allowChannelUseAlternativeCharges;

    public SurchargesDTO() {
    }

    public SurchargesDTO(SurchargeTypeDTO type, SurchargeLimitDTO limit, List<RangeDTO> ranges) {
        this.type = type;
        this.limit = limit;
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

    public SurchargeLimitDTO getLimit() {
        return limit;
    }

    public void setLimit(SurchargeLimitDTO limit) {
        this.limit = limit;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }

        if(!(obj instanceof SurchargesDTO)) {
            return false;
        }

        return ((SurchargesDTO) obj).getType().equals(this.getType());
    }
}
